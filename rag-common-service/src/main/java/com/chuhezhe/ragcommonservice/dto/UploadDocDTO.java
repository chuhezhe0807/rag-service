package com.chuhezhe.ragcommonservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
* 上传文件DTO
 */
@Data
@AllArgsConstructor
public class UploadDocDTO {

    /**
     * 知识库id
     */
    private Integer knowledgeBaseId;

    /**
     * 上传到minio的bucketName
     */
    private String bucketName;

    /**
     * 上传到minio的objectName
     */
    private String objectName;

    /**
     * 文件
     */
    private MultipartFile file;
}
