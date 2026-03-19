package com.chuhezhe.ragcommonservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
public class UploadDocRecordDTO {
    /**
     * 关联知识库ID
     */
    @NotBlank(message = "关联知识库ID不能为空")
    private Integer knowledgeBaseId;

    /**
     * 文件名
     */
    @NotBlank(message = "文件名不能为空")
    private String fileName;

    /**
     * 文件哈希值
     */
    @NotBlank(message = "文件哈希值不能为空")
    private String fileHash;

    /**
     * 文件大小（字节）
     */
    @NotBlank(message = "文件大小不能为空")
    private BigInteger fileSize;

    /**
     * MIME类型
     */
    @NotBlank(message = "MIME类型不能为空")
    private String contentType;

    /**
     * 临时文件路径
     */
    @NotBlank(message = "临时文件路径不能为空")
    private String tempPath;
}
