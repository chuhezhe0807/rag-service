package com.chuhezhe.ragdocumentservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("`documents`")
public class Document {
    /**
     * 主键ID，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 文件在MinIO中的路径，非空
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 实际文件名，非空
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件大小（字节），非空
     */
    @TableField("file_size")
    private BigInteger fileSize;

    /**
     * MIME类型，非空
     */
    @TableField("content_type")
    private String contentType;

    /**
     * 文件SHA-256哈希，带索引
     */
    @TableField("file_hash")
    private String fileHash;

    /**
     * 关联知识库ID，外键关联knowledge_bases表，非空
     */
    @TableField("knowledge_base_id")
    private Integer knowledgeBaseId;

    /**
     * 创建时间，默认当前时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间，默认当前时间，更新时自动更新
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
