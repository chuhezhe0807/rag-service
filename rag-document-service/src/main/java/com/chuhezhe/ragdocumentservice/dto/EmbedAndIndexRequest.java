package com.chuhezhe.ragdocumentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 发送给 rag-ai-service /internal/embed-and-index 的请求体。
 * 结构对齐 Python EmbedAndIndexRequest：{ kb_id: int, chunks: [{id,content,metadata}] }。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbedAndIndexRequest {

    private Integer kbId;

    private List<Chunk> chunks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Chunk {
        /** chunk 业务 id（document_chunks.id 的 UUID） */
        private String id;

        /** chunk 文本内容，对应 langchain Document.page_content */
        private String content;

        /** 任意 metadata；常用：source/kb_id/document_id/chunk_id/page_content */
        private Map<String, Object> metadata;
    }
}
