package com.chuhezhe.ragdocumentservice.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuhezhe.ragcommonservice.dto.QueryDocDTO;
import com.chuhezhe.ragdocumentservice.entity.Document;
import com.chuhezhe.ragdocumentservice.mapper.DocumentMapper;
import com.chuhezhe.ragcommonservice.vo.DocumentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService extends ServiceImpl<DocumentMapper, Document> {

    /**
     * 查询文档
     */
    public List<DocumentVO> queryDocument(QueryDocDTO queryDocDTO) {
        List<Document> documents = baseMapper.queryDocument(queryDocDTO);
        List<DocumentVO> res = new ArrayList<>();

        for (Document document : documents) {
            DocumentVO documentVO = new DocumentVO();
            BeanUtils.copyProperties(document, documentVO);
            res.add(documentVO);
        }

        return res;
    }
}
