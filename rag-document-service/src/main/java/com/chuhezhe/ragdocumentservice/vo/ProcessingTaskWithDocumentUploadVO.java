package com.chuhezhe.ragdocumentservice.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.chuhezhe.ragdocumentservice.entity.DocumentUpload;
import com.chuhezhe.ragdocumentservice.entity.ProcessingTask;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 处理任务VO 包含文档上传信息
 */
@Data
@NoArgsConstructor
public class ProcessingTaskWithDocumentUploadVO extends ProcessingTask {

    @TableField(exist = false) // 不存在于数据库字段，仅用于VO
    private DocumentUpload documentUploadVO;
}
