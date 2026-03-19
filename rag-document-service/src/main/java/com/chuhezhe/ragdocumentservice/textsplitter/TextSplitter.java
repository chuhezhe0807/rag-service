package com.chuhezhe.ragdocumentservice.textsplitter;

import com.chuhezhe.ragdocumentservice.loader.FileLoader;

import java.util.List;

public interface TextSplitter {
    List<FileLoader.DocumentChunk> splitDocuments(List<FileLoader.DocumentChunk> documentChunks);
}
