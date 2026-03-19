package com.chuhezhe.ragdocumentservice.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {
    @Value("${ai.embedding-model.api-key}")
    private String apiKey;

    @Bean("embeddingModel")
    public EmbeddingModel embeddingModel() {
        return new OpenAiEmbeddingModel(
                OpenAiApi.builder().apiKey(apiKey).build()
        );
    }
}
