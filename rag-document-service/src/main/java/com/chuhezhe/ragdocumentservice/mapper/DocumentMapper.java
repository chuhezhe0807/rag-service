package com.chuhezhe.ragdocumentservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chuhezhe.ragcommonservice.dto.QueryDocDTO;
import com.chuhezhe.ragdocumentservice.entity.Document;

import java.util.List;

public interface DocumentMapper extends BaseMapper<Document> {
    /**
     * 查询文档
     */
    List<Document> queryDocument(QueryDocDTO queryDocDTO);
}
