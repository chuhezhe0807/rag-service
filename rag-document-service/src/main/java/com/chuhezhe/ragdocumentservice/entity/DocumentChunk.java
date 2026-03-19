package com.chuhezhe.ragdocumentservice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("`document_chunks`")
public class DocumentChunk {
    @TableId(value = "id")
    private String id;

    @TableField("document_id")
    private Integer documentId;

    @TableField("kb_id")
    private Integer kbId;

    @TableField("file_name")
    private String fileName;

    @TableField(value = "chunk_metadata", typeHandler = JacksonTypeHandler.class)
    private ChunkMetadata chunkMetadata;

    @TableField("hash")
    private String hash;

    @Data
    @Builder
    public static class ChunkMetadata {
        private String source;

        private Integer kb_id;

        private Integer document_id;

        private String chunk_id;

        private String page_content;
    }
}