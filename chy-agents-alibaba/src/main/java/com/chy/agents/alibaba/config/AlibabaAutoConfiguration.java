package com.chy.agents.alibaba.config;

import com.chy.agents.core.chat.ChatClient;
import com.chy.agents.alibaba.client.AlibabaChatClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

/**
 * 阿里云通义自动配置
 */
@AutoConfiguration
@EnableConfigurationProperties(AlibabaConfig.class)
@ConditionalOnProperty(prefix = "chy.agents.alibaba", name = "api-key")
public class AlibabaAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public ChatClient alibabaChatClient(@Validated AlibabaConfig config) {
        // 在创建客户端之前验证配置
        config.validate();
        return new AlibabaChatClient(config);
    }
} 