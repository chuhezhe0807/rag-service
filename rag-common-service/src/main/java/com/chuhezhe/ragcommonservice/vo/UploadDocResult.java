package com.chuhezhe.ragcommonservice.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
public class UploadDocResult {
    /**
     * 上传文档记录ID
     */
    private Integer uploadDocRecordId;

    /**
     * 原始文件名
     */
    private String originalFilename;

    /**
     * 文件访问URL
     */
    private String url;

    /**
     * 文件hash
     */
    private String fileHash;

    /**
     * 文件大小
     */
    private BigInteger fileSize;

    /**
     * 文件内容类型
     */
    private String contentType;

    /**
     * 临时的文件路径
     */
    private String tempPath;
}