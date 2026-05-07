package com.chuhezhe.ragdocumentservice.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class AIConfig {
    @Value("${ai.embedding-model.api-key:}")
    private String apiKey;

    @Bean("embeddingModel")
    public EmbeddingModel embeddingModel() {
        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException(
                    "ai.embedding-model.api-key is blank. "
                            + "Set the OPENAI_API_KEY environment variable (or override "
                            + "ai.embedding-model.api-key in Nacos config rag-document-service.yaml) "
                            + "before starting rag-document-service."
            );
        }
        return new OpenAiEmbeddingModel(
                OpenAiApi.builder().apiKey(apiKey).build()
        );
    }
}
