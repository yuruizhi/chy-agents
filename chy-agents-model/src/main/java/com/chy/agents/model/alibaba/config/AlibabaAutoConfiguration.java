package com.chy.agents.model.alibaba.config;

import com.chy.agents.core.chat.ChatClient;
import com.chy.agents.model.alibaba.client.AlibabaChatClient;
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
    @ConditionalOnMissingBean(name = "alibabaChatClient")
    public ChatClient alibabaChatClient(@Validated AlibabaConfig config) {
        try {
            // 在创建客户端之前验证配置
            config.validate();
            return new AlibabaChatClient(config);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize AlibabaChatClient", e);
        }
    }
} 