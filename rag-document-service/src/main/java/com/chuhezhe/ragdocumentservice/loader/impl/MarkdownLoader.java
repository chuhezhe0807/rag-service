package com.chuhezhe.ragdocumentservice.loader.impl;

import com.chuhezhe.ragdocumentservice.loader.FileLoader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用CommonMark解析库提取文本
 */
public class MarkdownLoader implements FileLoader {
    @Override
    public List<DocumentChunk> load(Path filePath) throws Exception {
        List<DocumentChunk> documentChunks = new ArrayList<>();

        String content = Files.readString(filePath, StandardCharsets.UTF_8);

        // 创建元数据
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", filePath.toString());
        metadata.put("file_name", filePath.getFileName().toString());
        metadata.put("format", "markdown");

        // 创建Document对象，使用原始Markdown内容作为pageContent
        DocumentChunk doc = new DocumentChunk(content, metadata);
        documentChunks.add(doc);

        return documentChunks;
    }
}
