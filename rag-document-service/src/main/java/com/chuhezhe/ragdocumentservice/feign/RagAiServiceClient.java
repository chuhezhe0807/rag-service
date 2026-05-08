package com.chuhezhe.ragdocumentservice.feign;

import com.chuhezhe.ragdocumentservice.dto.EmbedAndIndexRequest;
import com.chuhezhe.ragdocumentservice.vo.EmbedAndIndexResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * US-010 Option B：Java 写完 MySQL chunk 后，把 chunk 内容送到 Python
 * rag-ai-service 的 /internal/embed-and-index；Python 用 EmbeddingsFactory
 * 做 embedding 并写进 ChromaDB 的 `kb_{kb_id}` collection。
 *
 * 走 Nacos 服务发现，不要把 URL 硬编码到 bootstrap.yml 里。
 */
@FeignClient(name = "rag-ai-service")
public interface RagAiServiceClient {

    @PostMapping("/internal/embed-and-index")
    EmbedAndIndexResponse embedAndIndex(@RequestBody EmbedAndIndexRequest request);
}
