package com.chuhezhe.ragdocumentservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chuhezhe.ragdocumentservice.entity.ProcessingTask;
import com.chuhezhe.ragdocumentservice.vo.ProcessingTaskWithDocumentUploadVO;

public interface ProcessingTaskMapper extends BaseMapper<ProcessingTask> {

    ProcessingTaskWithDocumentUploadVO selectTaskWithDocumentUploadById(Integer id);
}
