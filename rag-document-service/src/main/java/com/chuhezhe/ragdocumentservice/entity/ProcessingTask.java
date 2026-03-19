package com.chuhezhe.ragdocumentservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 处理任务实体类
 * 对应数据库表：processing_tasks
 */
@Data
@Builder
@TableName("processing_tasks")  // 指定数据库表名（若类名和表名一致可省略）
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProcessingTask {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)  // 主键自增（对应SQLAlchemy的primary_key=True）
    private Integer id;

    /**
     * 知识库ID（关联knowledge_bases表）
     */
    @TableField("knowledge_base_id")  // 指定数据库字段名（驼峰匹配可省略，此处显式标注更清晰）
    private Integer knowledgeBaseId;

    /**
     * 文档ID（关联documents表，可为空）
     */
    @TableField(value = "document_id", exist = true)  // exist=true表示字段存在（默认true）
    private Integer documentId;

    /**
     * 文档上传ID（关联document_uploads表，可为空）
     */
    @TableField("document_upload_id")
    private Integer documentUploadId;

    /**
     * 任务状态：pending(待处理), processing(处理中), completed(已完成), failed(失败)
     * 默认值：pending（MP的默认值需结合数据库默认值或业务代码）
     */
    @TableField("status")
    private String status;

    /**
     * 错误信息（可为空）
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 创建时间
     * fill = FieldFill.INSERT：插入时自动填充
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     * fill = FieldFill.INSERT_UPDATE：插入和更新时自动填充
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // ========== 可选：状态常量（推荐定义，避免硬编码） ==========
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_FAILED = "failed";
}