package com.chy.agents.openai.config;

import com.chy.agents.core.chat.ChatClient;
import com.chy.agents.openai.client.OpenAiChatClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAI自动配置
 */
@Configuration
@ConditionalOnProperty(prefix = "chy.agents.openai", name = "api-key")
public class OpenAiAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public OpenAiApi openAiApi(OpenAiConfig config) {
        return new OpenAiApi(config.getApiKey());
    }
    
    @Bean
    @ConditionalOnMissingBean
    public ChatClient openAiChatClient(OpenAiApi openAiApi, OpenAiConfig config) {
        return new OpenAiChatClient(openAiApi, config);
    }
} 