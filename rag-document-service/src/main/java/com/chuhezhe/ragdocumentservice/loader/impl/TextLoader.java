package com.chuhezhe.ragdocumentservice.loader.impl;

import com.chuhezhe.ragdocumentservice.loader.FileLoader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextLoader implements FileLoader {
    @Override
    public List<DocumentChunk> load(Path path) throws Exception {
        List<DocumentChunk> documentChunks = new ArrayList<>();

        // 直接读取文本文件内容
        String content = Files.readString(path, StandardCharsets.UTF_8);

        // 创建元数据
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", path.toString());
        metadata.put("file_name", path.getFileName().toString());
        metadata.put("format", "text");

        // 创建Document对象
        DocumentChunk doc = new DocumentChunk(content, metadata);
        documentChunks.add(doc);

        return documentChunks;
    }
}
