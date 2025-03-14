package com.chy.agents.common.config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
    @ConditionalOnProperty(prefix = "spring.ai.openai", name = "api-key")
    public OpenAiApi openAiApi(ModelConfig modelConfig) {
        return new OpenAiApi(modelConfig.getOpenai().getApiKey());
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.ai.openai", name = "api-key")
    public ChatClient openAiChatClient(OpenAiApi openAiApi, ModelConfig modelConfig) {
        // 创建OpenAiChatOptions实例，并设置模型和温度
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel(modelConfig.getOpenai().getChatModel())
                .withTemperature(Double.valueOf(modelConfig.getTemperature()))
                .build();
        // 使用OpenAiApi和OpenAiChatOptions创建OpenAiChatClient实例
        return new OpenAiChatClient(openAiApi, options);
    }
} 