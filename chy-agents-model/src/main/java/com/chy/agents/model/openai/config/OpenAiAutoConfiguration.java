package com.chy.agents.model.openai.config;

import com.chy.agents.core.chat.ChatClient;
import com.chy.agents.core.chat.adapter.SpringAiChatClientFactory;
import com.chy.agents.model.openai.client.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

/**
 * OpenAI自动配置
 */
@AutoConfiguration
@EnableConfigurationProperties(OpenAiConfig.class)
@ConditionalOnProperty(prefix = "chy.agents.openai", name = "api-key")
@ConditionalOnClass(OpenAiApi.class)
public class OpenAiAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public OpenAiApi openAiApi(@Validated OpenAiConfig config) {
        OpenAiApi.Builder builder = OpenAiApi.builder()
            .withApiKey(config.getApiKey());
        
        if (config.getOrganizationId() != null) {
            builder.withOrganization(config.getOrganizationId());
        }
        
        if (config.getEndpoint() != null && !config.getEndpoint().equals("https://api.openai.com")) {
            builder.withBaseUrl(config.getEndpoint());
        }
        
        return builder.build();
    }
    
    @Bean
    @ConditionalOnMissingBean(name = "springAiOpenAiChatClient")
    public OpenAiChatClient springAiOpenAiChatClient(OpenAiApi openAiApi, @Validated OpenAiConfig config) {
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel(config.getModel())
                .withTemperature(config.getTemperature())
                .withTopP(config.getTopP())
                .withMaxTokens(config.getMaxTokens())
                .build();
        
        return new OpenAiChatClient(openAiApi, options);
    }
    
    @Bean
    @ConditionalOnMissingBean(name = "openAiChatClient")
    public ChatClient openAiChatClient(OpenAiChatClient springAiOpenAiChatClient, 
                                      SpringAiChatClientFactory factory,
                                      @Validated OpenAiConfig config) {
        try {
            config.validate();
            // 使用适配器工厂创建适配器
            return factory.createOpenAiAdapter(springAiOpenAiChatClient, config.getModel());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize OpenAiChatClient", e);
        }
    }
} 