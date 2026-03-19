package com.chuhezhe.ragdocumentservice.feign;

import com.chuhezhe.common.entity.Result;
import com.chuhezhe.ragcommonservice.vo.KnowledgeBaseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "rag-knowledge-service")
public interface KBClient {

    /**
     * 获取指定知识库
     */
    @GetMapping("/{kb_id}")
    Result<List<KnowledgeBaseVO>> getKnowledgeBase(@PathVariable("kb_id") Integer kbId);
}
