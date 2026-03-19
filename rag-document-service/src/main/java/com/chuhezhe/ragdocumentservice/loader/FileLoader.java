package com.chuhezhe.ragdocumentservice.loader;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface FileLoader {
    List<DocumentChunk> load(Path path) throws Exception;

    /**
     * 文档记录，包含页面内容和元数据
     * @param pageContent 页面内容
     * @param metadata 元数据
     */
    record DocumentChunk(String pageContent, Map<String, Object> metadata) {}
}
