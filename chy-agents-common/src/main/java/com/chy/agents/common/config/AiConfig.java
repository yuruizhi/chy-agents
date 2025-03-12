package com.chy.agents.common.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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