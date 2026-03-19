package com.chuhezhe.ragdocumentservice.textsplitter.impl;

import com.chuhezhe.ragdocumentservice.loader.FileLoader;
import com.chuhezhe.ragdocumentservice.textsplitter.TextSplitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 递归字符文本拆分器，基于LangChain的RecursiveCharacterTextSplitter实现
 */
public class RecursiveCharacterTextSplitter implements TextSplitter {
    /**
     * 每个文档块的最大字符数
     */
    private final int chunkSize;
    /**
     * 文档块之间的重叠字符数
     */
    private final int chunkOverlap;
    /**
     * 分隔符优先级列表
     */
    private final List<String> separators;

    public RecursiveCharacterTextSplitter(int chunkSize, int chunkOverlap) {
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
        // 定义分隔符优先级：段落 > 句子 > 单词 > 字符
        this.separators = List.of("\n\n", "\n", " ", "");
    }

    @Override
    public List<FileLoader.DocumentChunk> splitDocuments(List<FileLoader.DocumentChunk> documentChunks) {
        List<FileLoader.DocumentChunk> chunks = new ArrayList<>();

        for (FileLoader.DocumentChunk documentChunk : documentChunks) {
            String content = documentChunk.pageContent();
            Map<String, Object> metadata = documentChunk.metadata();
            
            // 递归分割文本
            List<String> splits = splitText(content);
            
            // 为每个分割创建文档块
            for (int i = 0; i < splits.size(); i++) {
                String chunkContent = splits.get(i);
                
                // 创建块的元数据，继承原始文档的元数据并添加块特定信息
                Map<String, Object> chunkMetadata = new HashMap<>(metadata);
                chunkMetadata.put("chunk_index", i);
                chunkMetadata.put("chunk_size", chunkContent.length());
                
                // 创建文档块
                FileLoader.DocumentChunk chunk = new FileLoader.DocumentChunk(chunkContent, chunkMetadata);
                chunks.add(chunk);
            }
        }

        return chunks;
    }

    /**
     * 递归分割文本
     */
    private List<String> splitText(String text) {
        List<String> finalChunks = new ArrayList<>();
        String separator = "";
        
        // 尝试使用不同的分隔符
        for (String s : separators) {
            if (s.isEmpty() || text.contains(s)) {
                separator = s;
                break;
            }
        }
        
        List<String> splits = splitTextWithSeparator(text, separator);
        
        // 处理每个分割
        for (String split : splits) {
            if (split.length() <= chunkSize) {
                finalChunks.add(split);
            } else {
                // 如果分割仍然太大，递归分割
                if (separator.equals(separators.get(separators.size() - 1))) {
                    // 已经是最后一个分隔符（空字符串），直接按字符分割
                    finalChunks.addAll(splitTextBySize(split));
                } else {
                    // 尝试使用下一个分隔符
                    finalChunks.addAll(splitText(split));
                }
            }
        }
        
        // 合并相邻的块，确保它们不超过chunkSize
        return mergeSplits(finalChunks);
    }

    /**
     * 使用指定分隔符分割文本
     */
    private List<String> splitTextWithSeparator(String text, String separator) {
        List<String> splits = new ArrayList<>();
        if (separator.isEmpty()) {
            for (int i = 0; i < text.length(); i++) {
                splits.add(text.substring(i, i + 1));
            }
        } else {
            String[] parts = text.split(separator);
            for (int i = 0; i < parts.length; i++) {
                splits.add(parts[i]);
                if (i < parts.length - 1) {
                    splits.add(separator);
                }
            }
        }
        return splits;
    }

    /**
     * 按大小分割文本
     */
    private List<String> splitTextBySize(String text) {
        List<String> splits = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            splits.add(text.substring(start, end));
            start = end;
        }
        return splits;
    }

    /**
     * 合并分割，确保块大小合适且有重叠
     */
    private List<String> mergeSplits(List<String> splits) {
        List<String> merged = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        List<String> currentChunkSplits = new ArrayList<>();
        
        for (String split : splits) {
            if (currentChunk.length() + split.length() <= chunkSize) {
                currentChunk.append(split);
                currentChunkSplits.add(split);
            } else {
                if (!currentChunk.isEmpty()) {
                    merged.add(currentChunk.toString());
                    
                    // 处理重叠
                    while (currentChunk.length() > chunkOverlap && !currentChunkSplits.isEmpty()) {
                        currentChunk.delete(0, currentChunkSplits.get(0).length());
                        currentChunkSplits.remove(0);
                    }
                }
                
                currentChunk = new StringBuilder(split);
                currentChunkSplits = new ArrayList<>();
                currentChunkSplits.add(split);
            }
        }
        
        if (!currentChunk.isEmpty()) {
            merged.add(currentChunk.toString());
        }
        
        return merged;
    }
}