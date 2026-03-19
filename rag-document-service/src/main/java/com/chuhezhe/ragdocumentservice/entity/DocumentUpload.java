package com.chuhezhe.ragdocumentservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 文档上传记录实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("`document_uploads`")
public class DocumentUpload {
    /**
     * 主键ID，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联知识库ID，外键关联knowledge_bases表，删除时级联删除，非空
     */
    @TableField("knowledge_base_id")
    private Integer knowledgeBaseId;

    /**
     * 文件名，非空
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件哈希值，非空
     */
    @TableField("file_hash")
    private String fileHash;

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
     * 临时文件路径，非空
     */
    @TableField("temp_path")
    private String tempPath;

    /**
     * 创建时间，非空，数据库默认当前时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 任务状态，非空，默认值为pending
     */
    @TableField("status")
    private String status;

    /**
     * 错误信息，可为空
     */
    @TableField("error_message")
    private String errorMessage;
}
