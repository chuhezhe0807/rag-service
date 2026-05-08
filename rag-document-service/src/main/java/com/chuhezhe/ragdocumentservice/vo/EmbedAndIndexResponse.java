package com.chuhezhe.ragdocumentservice.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * rag-ai-service /internal/embed-and-index 的返回结构。
 * 对齐 Python EmbedAndIndexResponse：{ indexed: int, collection: str }。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbedAndIndexResponse {

    /** 实际入向量库的 chunk 条数 */
    private Integer indexed;

    /** chroma collection 名，例如 kb_42 */
    private String collection;
}
