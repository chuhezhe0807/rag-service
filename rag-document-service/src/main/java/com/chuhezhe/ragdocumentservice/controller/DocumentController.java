package com.chuhezhe.ragdocumentservice.controller;

import com.chuhezhe.common.constants.ErrorConstants;
import com.chuhezhe.common.entity.Result;
import com.chuhezhe.ragcommonservice.dto.UploadDocRecordDTO;
import com.chuhezhe.ragcommonservice.dto.UploadDocDTO;
import com.chuhezhe.ragcommonservice.vo.UploadDocResult;
import com.chuhezhe.ragdocumentservice.dto.PreviewDocDTO;
import com.chuhezhe.ragdocumentservice.dto.ProcessDocDTO;
import com.chuhezhe.ragcommonservice.dto.QueryDocDTO;
import com.chuhezhe.ragdocumentservice.service.DocumentProcessorService;
import com.chuhezhe.ragdocumentservice.service.DocumentService;
import com.chuhezhe.ragcommonservice.vo.DocumentVO;
import com.chuhezhe.ragdocumentservice.service.DocumentUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/documents")
@RequiredArgsConstructor
public class DocumentController {
    // 文档处理器服务
    private final DocumentProcessorService documentProcessorService;

    // 文档服务
    private final DocumentService documentService;

    // 文档上传服务
    private final DocumentUploadService documentUploadService;

    // 上传文档
    @PostMapping("/upload")
    public Result<UploadDocResult> uploadDocument(@RequestBody UploadDocDTO uploadDocDTO) {
        Result<UploadDocResult> uploadFileToMiniIOResultResult = documentUploadService.uploadFileToMinIO(uploadDocDTO);

        UploadDocResult data = uploadFileToMiniIOResultResult.getData();
        Result<Integer> uploadRecordIdRes = documentUploadService.addUploadRecord(
                UploadDocRecordDTO.builder()
                        .knowledgeBaseId(uploadDocDTO.getKnowledgeBaseId())
                        .fileHash(data.getFileHash())
                        .fileName(data.getOriginalFilename())
                        .contentType(data.getContentType())
                        .fileSize(data.getFileSize())
                        .tempPath(data.getTempPath())
                        .build()
        );

        uploadFileToMiniIOResultResult.getData().setUploadDocRecordId(uploadRecordIdRes.getData());

        return uploadFileToMiniIOResultResult;
    }

    // 预览文档
    @PostMapping("/preview")
    public void previewDocument(@RequestBody PreviewDocDTO previewDTO) {

    }

    // 处理文档，返回任务id
    @PostMapping("/process")
    public Result<Map<String, List<Map<String, Integer>>>> processDocument(@RequestBody ProcessDocDTO processDocDTO) {
        return documentProcessorService.process(processDocDTO);
    }

    // 查询文档处理任务状态
    @GetMapping("/process/{taskId}")
    public Result<String> queryProcessStatus(@PathVariable String taskId) {
        return documentProcessorService.queryProcessStatus(taskId);
    }

    // 获取文档
    @GetMapping("/query")
    public Result<List<DocumentVO>> queryDocument(@RequestBody QueryDocDTO queryDocDTO) {
        if (queryDocDTO.getKnowledgeBaseId() == null && queryDocDTO.getFileHash() == null &&
                queryDocDTO.getFileName() == null) {
            return Result.error(ErrorConstants.PARAMETER_ERROR, "knowledgeBaseId, fileHash, fileName 不能同时为空");
        }

        List<DocumentVO> documentVOs = documentService.queryDocument(queryDocDTO);

        return Result.ok(documentVOs);
    }

}
