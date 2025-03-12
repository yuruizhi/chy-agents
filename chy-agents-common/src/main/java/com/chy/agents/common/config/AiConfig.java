package com.chy.agents.common.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI配置类。
 *
 * @author YuRuizhi
 * @date 2025/3/12
 */
@Configuration
public class AiConfig {

    @Bean
    public OpenAiApi openAiApi(OpenAiProperties properties) {
        return new OpenAiApiClient(properties.getApiKey());
    }

    @Bean
    public ChatClient chatClient(OpenAiApi openAiApi, OpenAiProperties properties) {
        // 创建OpenAiChatOptions实例，并设置模型和温度
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel(properties.getChatModel())
                .withTemperature(properties.getTemperature())
                .build();
        // 使用OpenAiApi和OpenAiChatOptions创建OpenAiChatClient实例
        return new OpenAiChatClient(openAiApi, options);
    }
    
    @Bean
    public ImageClient imageClient(OpenAiApi openAiApi, OpenAiProperties properties) {
        // 使用OpenAiApi创建OpenAiImageClient实例
        return new OpenAiImageClient(openAiApi);
    }
    
    @Bean
    public EmbeddingClient embeddingClient(OpenAiApi openAiApi) {
        // 使用OpenAiApi创建OpenAiEmbeddingClient实例
        return new OpenAiEmbeddingClient(openAiApi);
    }
    
    @Bean
    public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingClient embeddingClient) {
        // 使用JdbcTemplate和EmbeddingClient创建PgVectorStore实例
        return new PgVectorStore(jdbcTemplate, embeddingClient);
    }
} 