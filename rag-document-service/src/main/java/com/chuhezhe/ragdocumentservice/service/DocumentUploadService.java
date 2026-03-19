package com.chuhezhe.ragdocumentservice.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuhezhe.common.entity.Result;
import com.chuhezhe.ragcommonservice.dto.UploadDocRecordDTO;
import com.chuhezhe.ragcommonservice.dto.UploadDocDTO;
import com.chuhezhe.ragcommonservice.vo.UploadDocResult;
import com.chuhezhe.ragdocumentservice.entity.DocumentUpload;
import com.chuhezhe.ragdocumentservice.mapper.DocumentUploadMapper;
import com.chuhezhe.ragdocumentservice.util.MinIOClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentUploadService extends ServiceImpl<DocumentUploadMapper, DocumentUpload> {

    // minio客户端
    private final MinIOClient minIOClient;

    /**
     * 上传文档到minio存储桶
     */
     public Result<UploadDocResult> uploadFileToMinIO(UploadDocDTO uploadDocDTO) {
         UploadDocResult uploadDocResult = minIOClient.uploadFile(
                 uploadDocDTO.getFile(),
                 uploadDocDTO.getBucketName(),
                 uploadDocDTO.getObjectName()
         );

        return Result.ok(uploadDocResult);
    }

    /**
     * 在表中添加文档上传记录
     * @param uploadDocRecordDTO 上传文档DTO
     * @return 上传记录ID
     */
    public Result<Integer> addUploadRecord (UploadDocRecordDTO uploadDocRecordDTO) {
        DocumentUpload documentUpload = DocumentUpload.builder()
                .knowledgeBaseId(uploadDocRecordDTO.getKnowledgeBaseId())
                .fileName(uploadDocRecordDTO.getFileName())
                .fileHash(uploadDocRecordDTO.getFileHash())
                .fileSize(uploadDocRecordDTO.getFileSize())
                .contentType(uploadDocRecordDTO.getContentType())
                .tempPath(uploadDocRecordDTO.getTempPath())
                .build();

        baseMapper.insert(documentUpload);

        return Result.ok(documentUpload.getId());
    }
}
