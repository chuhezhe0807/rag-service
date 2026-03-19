package com.chuhezhe.ragdocumentservice.loader.impl;

import com.chuhezhe.ragdocumentservice.loader.FileLoader;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ooxml.extractor.POIXMLPropertiesTextExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用Apache POI提取文本
 */
public class DocxLoader implements FileLoader {
    @Override
    public List<DocumentChunk> load(Path filePath) throws Exception {
        List<DocumentChunk> documentChunks = new ArrayList<>();

        try (XWPFDocument document = new XWPFDocument(Files.newInputStream(filePath))) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            String text = extractor.getText();
            POIXMLPropertiesTextExtractor metadataTextExtractor = extractor.getMetadataTextExtractor();
            POIXMLProperties.CoreProperties metaData = metadataTextExtractor.getCoreProperties();

            // 创建元数据
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("source", filePath.toString());
            metadata.put("file_name", filePath.getFileName().toString());

            // 添加文档属性作为元数据
            metadata.put("title", metaData.getTitle());
            metadata.put("author", metaData.getCreator());
            metadata.put("subject", metaData.getSubject());
            metadata.put("keywords", metaData.getKeywords());

            // 创建Document对象
            DocumentChunk doc = new DocumentChunk(text, metadata);
            documentChunks.add(doc);
        }

        return documentChunks;
    }
}
