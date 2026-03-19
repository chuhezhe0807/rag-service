package com.chuhezhe.ragdocumentservice.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuhezhe.common.constants.ErrorConstants;
import com.chuhezhe.common.entity.Result;
import com.chuhezhe.common.util.MessageDigestUtil;
import com.chuhezhe.ragcommonservice.vo.KnowledgeBaseVO;
import com.chuhezhe.ragdocumentservice.dto.ProcessDocDTO;
import com.chuhezhe.ragdocumentservice.entity.Document;
import com.chuhezhe.ragdocumentservice.entity.DocumentChunk;
import com.chuhezhe.ragdocumentservice.entity.DocumentUpload;
import com.chuhezhe.ragdocumentservice.entity.ProcessingTask;
import com.chuhezhe.ragdocumentservice.feign.KBClient;
import com.chuhezhe.ragdocumentservice.loader.FileLoader;
import com.chuhezhe.ragdocumentservice.loader.impl.DocxLoader;
import com.chuhezhe.ragdocumentservice.loader.impl.MarkdownLoader;
import com.chuhezhe.ragdocumentservice.loader.impl.PDFLoader;
import com.chuhezhe.ragdocumentservice.loader.impl.TextLoader;
import com.chuhezhe.ragdocumentservice.mapper.ProcessingTaskMapper;
import com.chuhezhe.ragdocumentservice.textsplitter.TextSplitter;
import com.chuhezhe.ragdocumentservice.textsplitter.impl.RecursiveCharacterTextSplitter;
import com.chuhezhe.ragdocumentservice.util.MinIOClient;
import com.chuhezhe.ragdocumentservice.vo.ProcessingTaskWithDocumentUploadVO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentProcessorService extends ServiceImpl<ProcessingTaskMapper, ProcessingTask> {
    private Map<String, FileLoader>  documentLoaders;

    private TextSplitter textSplitter;

    private final DocumentUploadService documentUploadService;

    private final DocumentService documentService;

    private final DocumentChunkService documentChunkService;

    private final MinIOClient minIOClient;

    private final VectorStore vectorStore;

    /**
     * 知识库客户端
     */
    private final KBClient kbClient;

    @PostConstruct // 会在bean初始化完成后调用
    public void init() {
        documentLoaders = new HashMap<>();

        // 初始化各种加载器
        documentLoaders.put("pdf", new PDFLoader());
        documentLoaders.put("docx", new DocxLoader());
        documentLoaders.put("md", new MarkdownLoader());
        documentLoaders.put("txt", new TextLoader());

        // 初始化文本分割器
        textSplitter = new RecursiveCharacterTextSplitter(1000, 200);
    }

    /**
     * 处理文档
     */
    public Result<Map<String, List<Map<String, Integer>>>> process(ProcessDocDTO processDOCDTO) {
        // 1. 查询知识库
        Result<List<KnowledgeBaseVO>> kbResult = kbClient.getKnowledgeBase(processDOCDTO.getKnowledgeId());
        KnowledgeBaseVO kb = kbResult.getData().getFirst();

        if(!kbResult.isSuccess() || kb == null) {
            return Result.error(ErrorConstants.KNOWLEDGE_BASE_NOT_EXIST);
        }

        // 2. 准备要处理的文档的id
        List<Integer> uploadIds = processDOCDTO.getProcessDocItemDTOList().stream()
                        .filter(item -> !item.getSkipProcess())
                        .map(ProcessDocDTO.ProcessDocItemDTO::getDocumentUploadId)
                        .toList();

        if(uploadIds.isEmpty()) {
            return Result.ok(Map.of("tasks", Collections.emptyList()));
        }

        // 3. 找到uploadIds对应的 DocumentUpload 表中的记录
        List<DocumentUpload> documentUploads = documentUploadService.listByIds(uploadIds);

        // 4. 为每个文档创建一个处理任务记录
        List<Map<String, Integer>> taskInfo = new ArrayList<>();
        List<Map<String, String>> taskData = new ArrayList<>();
        documentUploads.forEach(documentUpload -> {
            ProcessingTask task = ProcessingTask.builder()
                    .knowledgeBaseId(processDOCDTO.getKnowledgeId())
                    .documentUploadId(documentUpload.getId())
                    .build();

            this.baseMapper.insert(task);

            taskInfo.add(Map.of("taskId", task.getId(), "uploadId", documentUpload.getId()));
            taskData.add(
                    Map.of(
                            "taskId", String.valueOf(task.getId()),
                            "uploadId", String.valueOf(documentUpload.getId()),
                            "tempPath", documentUpload.getTempPath(),
                            "fileName", documentUpload.getFileName()
                    )
            );
        });

        // 5. 添加到处理任务队列
        taskData.forEach(this::executeAsyncTask);

        return Result.ok(Map.of("tasks", taskInfo));
    }

    /**
     * 执行异步任务
     */
    @Async("taskExecutor")
    protected void executeAsyncTask(Map<String, String> taskData) {
        String taskId = taskData.get("taskId");
        String tempPath = taskData.get("tempPath");
        String fileName = taskData.get("fileName");
        String tempDir = System.getProperty("java.io.tmpdir");
        Path localTempFilePath = Paths.get(tempDir, String.format("%s_%s", taskId, fileName));

        log.info("Starting background processing for task {}, file: {}", taskId, fileName);

        ProcessingTaskWithDocumentUploadVO task = this.baseMapper.selectTaskWithDocumentUploadById(Integer.parseInt(taskId));

        if(task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }

        try {
            log.info("Start processing task {}, file: {}", taskId, fileName);
            task.setStatus(ProcessingTask.STATUS_PROCESSING);
            this.baseMapper.updateById(task);

            // 1. 从临时目录下载文件
            log.info("Task {}: Downloading file from MinIO: {} to {}", taskId, tempPath, localTempFilePath);

            try (InputStream inputStream = minIOClient.downloadFile(tempPath)) {
                OutputStream outputStream = Files.newOutputStream(localTempFilePath);
                FileCopyUtils.copy(inputStream, outputStream);
                log.info("Task {}: File downloaded successfully to {}", taskId, localTempFilePath);
            }
            catch (IOException e) {
                log.error("Task {}: Failed to download file from MinIO to {}", taskId, localTempFilePath, e);
                // 删除可能的残缺文件
                Files.deleteIfExists(localTempFilePath);
                throw new IOException("下载文件失败", e);
            }

            try {
                // 2. 加载和分块文档
                String extension = FilenameUtils.getExtension(fileName);
                log.info("Task {}: Loading document with extension {}", taskId, extension);

                FileLoader fileLoader = documentLoaders.get(extension);

                if(fileLoader == null) {
                    fileLoader = documentLoaders.get("txt"); // 默认使用文本加载器
                }

                log.info("Task {}: Loading document content from {}", taskId, localTempFilePath);
                List<FileLoader.DocumentChunk> documents = fileLoader.load(localTempFilePath);
                log.info("Task {}: Document loaded successfully with {} pages", taskId, documents.size());

                log.info("Task {}: Splitting document into chunks", taskId);
                List<FileLoader.DocumentChunk> documentChunks = textSplitter.splitDocuments(documents);
                log.info("Task {}: Document split into {} chunks", taskId, documentChunks.size());

                // 3. 将临时文件移动到永久目录
                String permanent_path = String.format("kb_%d/%s", task.getKnowledgeBaseId(), fileName);

                try {
                    log.info("Task {}: Moving file to permanent storage: {}", taskId, permanent_path);
                    minIOClient.copyFile(tempPath, permanent_path);
                    log.info("Task {}: File moved to permanent storage", taskId);

                    // 删除临时文件
                    log.info("Task {}: Removing temporary file from MinIO", taskId);
                    minIOClient.deleteFile(tempPath);
                    log.info("Task {}: Temporary file removed", taskId);
                }
                catch (Exception e) {
                    String error_msg = String.format("Failed to move file to permanent storage: %s", e.getMessage());
                    throw new Exception(error_msg);
                }

                // 4. 创建文档记录
                log.info("Task {}: Creating document record for {}", taskId, permanent_path);
                Document document = Document.builder()
                        .fileName(fileName)
                        .filePath(permanent_path)
                        .fileSize(task.getDocumentUploadVO().getFileSize())
                        .fileHash(task.getDocumentUploadVO().getFileHash())
                        .contentType(task.getDocumentUploadVO().getContentType())
                        .knowledgeBaseId(task.getKnowledgeBaseId())
                        .build();
                documentService.getBaseMapper().insert(document);
                log.info("Task {}: Document record created successfully", taskId);

                // 5. 存储文档块
                log.info("Task {}: Storing document chunks", taskId);
                List<DocumentChunk> documentChunkList = new ArrayList<>();
                for (int i = 0; i < documentChunks.size(); i++) {
                    FileLoader.DocumentChunk chunk = documentChunks.get(i);

                    // 为每个chunk生成唯一的id
                    String chunkId = UUID.randomUUID().toString();

                    DocumentChunk documentChunk = DocumentChunk.builder()
                            .id(chunkId)
                            .documentId(document.getId())
                            .kbId(task.getKnowledgeBaseId())
                            .fileName(fileName)
                            .hash(MessageDigestUtil.sha256((chunk.pageContent() + chunk.metadata().toString())))
                            .chunkMetadata(
                                    DocumentChunk.ChunkMetadata.builder()
                                            .source(fileName)
                                            .kb_id(task.getKnowledgeBaseId())
                                            .document_id(document.getId())
                                            .chunk_id(chunkId)
                                            .page_content(chunk.pageContent())
                                            .build()
                            )
                            .build();

                    documentChunkList.add(documentChunk);

                    if (i > 0 && i % 100 == 0) {
                        documentChunkService.saveBatch(documentChunkList, 100);
                        log.info("Task {}: Stored {} chunks", taskId, 100);
                        documentChunkList.clear();
                    }
                }

                if (!documentChunkList.isEmpty()) {
                    documentChunkService.saveBatch(documentChunkList, documentChunkList.size());
                    log.info("Task {}: Stored {} chunks", taskId, documentChunkList.size());
                }
                log.info("Task {}: All document chunks stored successfully", taskId);

                // 6. 添加到向量存储
                log.info("Task {}: Adding chunks to vector store", taskId);
                List<org.springframework.ai.document.Document> aiDocList = documentChunks.stream()
                        .map(chunk -> org.springframework.ai.document.Document.builder()
                                .text(chunk.pageContent())
                                .metadata(chunk.metadata())
                                .build()
                        )
                        .collect(Collectors.toList());
                vectorStore.add(aiDocList);
                log.info("Task {}: Chunks added to vector store", taskId);

                // 7. 更新任务状态
                task.setStatus(ProcessingTask.STATUS_COMPLETED);
                this.baseMapper.updateById(task);

                // 8. 更新上传记录状态
                DocumentUpload documentUploadVO = task.getDocumentUploadVO();
                documentUploadVO.setStatus(ProcessingTask.STATUS_COMPLETED);
                documentUploadService.getBaseMapper().updateById(documentUploadVO);
                log.info("Task {}: Updating upload record status to completed", taskId);

                log.info("Task {}: Processing completed successfully", taskId);
            }
            finally {
                try {
                    // 清理本地文件
                    Files.deleteIfExists(localTempFilePath);
                    log.info("Temp file cleaned: {}", localTempFilePath);
                }
                catch (IOException e) {
                    log.warn("Failed to clean temp file: {}", localTempFilePath, e);
                }
            }
        }
        catch (Exception e) {
            log.error("Task {}: Error processing document: {}", taskId, e.getMessage());
            task.setStatus(ProcessingTask.STATUS_FAILED);
            task.setErrorMessage(e.getMessage());
            this.baseMapper.updateById(task);

            // 清理临时文件
            try {
                minIOClient.deleteFile(tempPath);
                log.info("Task {}: Temporary file cleaned up after error", taskId);
            }
            catch (Exception ex) {
                log.warn("Task {}: Failed to clean up temporary file after error", taskId, ex);
            }
        }
    }

    // 查询文档处理任务状态
    public Result<String> queryProcessStatus(String taskId) {
        ProcessingTask task = this.baseMapper.selectById(taskId);

        if (task == null) {
            return Result.error(ErrorConstants.PARAMETER_ERROR, "task not found");
        }

        return Result.ok(task.getStatus());
    }
}
