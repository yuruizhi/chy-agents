package com.chy.agents.image.config;

import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAI配置类
 * 提供用于图像分析的OpenAI客户端
 */
@Configuration
public class OpenAiConfiguration {

    @Value("${spring.ai.openai.api-key:${OPENAI_API_KEY:}}")
    private String openAiApiKey;
    
    @Value("${spring.ai.openai.chat.model:gpt-4-vision-preview}")
    private String openAiModel;
    
    @Value("${spring.ai.openai.chat.max-tokens:1000}")
    private Integer maxTokens;
    
    /**
     * 创建OpenAI聊天客户端，用于图像分析
     * 使用GPT-4-Vision模型处理图像
     */
    @Bean(name = "openAiChatClient")
    public OpenAiChatClient openAiChatClient() {
        OpenAiApi api = new OpenAiApi(openAiApiKey);
        
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel(openAiModel)
                .withMaxTokens(maxTokens)
                .build();
        
        return new OpenAiChatClient(api, options);
    }
} 