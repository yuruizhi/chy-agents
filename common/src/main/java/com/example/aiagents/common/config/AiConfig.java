package com.example.aiagents.common.config;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.openai.OpenAiChatClient;
import org.springframework.ai.chat.openai.OpenAiChatOptions;
import org.springframework.ai.image.ImageClient;
import org.springframework.ai.image.openai.OpenAiImageClient;
import org.springframework.ai.openai.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiApiClient;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.embedding.openai.OpenAiEmbeddingClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class AiConfig {

    @Bean
    public OpenAiApi openAiApi(OpenAiProperties properties) {
        return new OpenAiApiClient(properties.getApiKey());
    }

    @Bean
    public ChatClient chatClient(OpenAiApi openAiApi, OpenAiProperties properties) {
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel(properties.getChatModel())
                .withTemperature(properties.getTemperature())
                .build();
        return new OpenAiChatClient(openAiApi, options);
    }
    
    @Bean
    public ImageClient imageClient(OpenAiApi openAiApi, OpenAiProperties properties) {
        return new OpenAiImageClient(openAiApi);
    }
    
    @Bean
    public EmbeddingClient embeddingClient(OpenAiApi openAiApi) {
        return new OpenAiEmbeddingClient(openAiApi);
    }
    
    @Bean
    public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingClient embeddingClient) {
        return new PgVectorStore(jdbcTemplate, embeddingClient);
    }
} 