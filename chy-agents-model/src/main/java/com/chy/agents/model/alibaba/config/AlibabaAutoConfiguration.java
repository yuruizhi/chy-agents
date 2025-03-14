package com.chy.agents.model.alibaba.config;

import com.chy.agents.core.chat.ChatClient;
import com.chy.agents.core.chat.adapter.SpringAiChatClientFactory;
import com.chy.agents.model.alibaba.client.AlibabaChatClient;
import com.alibaba.cloud.ai.dashscope.DashScopeChatClient;
import com.alibaba.cloud.ai.dashscope.DashScopeChatOptions;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

/**
 * 阿里云通义自动配置
 */
@AutoConfiguration
@EnableConfigurationProperties(AlibabaConfig.class)
@ConditionalOnProperty(prefix = "chy.agents.alibaba", name = "api-key")
@ConditionalOnClass({RestTemplate.class})
public class AlibabaAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean(name = "springAiAlibabaChatClient")
    public DashScopeChatClient springAiAlibabaChatClient(@Validated AlibabaConfig config) {
        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .withModel(config.getModel())
                .withApiKey(config.getApiKey())
                .withTemperature(config.getTemperature())
                .withTopP(config.getTopP())
                .build();
        
        return new DashScopeChatClient(options);
    }
    
    @Bean
    @ConditionalOnMissingBean(name = "alibabaChatClient")
    public ChatClient alibabaChatClient(DashScopeChatClient springAiAlibabaChatClient,
                                       SpringAiChatClientFactory factory,
                                       @Validated AlibabaConfig config) {
        try {
            // 在创建客户端之前验证配置
            config.validate();
            // 使用适配器工厂创建适配器
            return factory.createAlibabaAdapter(springAiAlibabaChatClient, config.getModel());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize AlibabaChatClient", e);
        }
    }
} 