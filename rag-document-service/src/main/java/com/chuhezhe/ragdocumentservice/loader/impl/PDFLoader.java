package com.chuhezhe.ragdocumentservice.loader.impl;

import com.chuhezhe.ragdocumentservice.loader.FileLoader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用PDFBox提取文本
 */
public class PDFLoader implements FileLoader {

    @Override
    public List<DocumentChunk> load(Path path) throws Exception {
        List<DocumentChunk> documentChunks = new ArrayList<>();

        try (PDDocument document = PDDocument.load(path.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            for (int page = 1; page <= document.getNumberOfPages(); page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                String text = stripper.getText(document);

                // 创建元数据
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("source", path.toString());
                metadata.put("page", page);
                metadata.put("total_pages", document.getNumberOfPages());
                metadata.put("file_name", path.getFileName().toString());

                // 创建Document对象
                DocumentChunk doc = new DocumentChunk(text, metadata);
                documentChunks.add(doc);
            }
        }

        return documentChunks;
    }
}
