package com.chuhezhe.ragcommonservice.vo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class DocumentVO {

    /**
     * id
     */
    private Integer id;

    /**
     * 文件在MinIO中的路径
     */
    private String filePath;

    /**
     * 实际文件名
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private BigInteger fileSize;

    /**
     * MIME类型
     */
    private String contentType;

    /**
     * 文件SHA-256哈希
     */
    private String fileHash;

    /**
     * 关联知识库ID
     */
    private Integer knowledgeBaseId;
}
