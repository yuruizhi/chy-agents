package com.chy.agents.core.config;

import org.springframework.ai.chat.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Qualifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模型路由配置
 * 
 * 变更记录：
 * 1.0.0-M6+ 支持多模型动态路由
 * 新增阿里云模型支持
 */
@Configuration
public class ModelRouterConfig {

    @Bean
    public ModelRouter modelRouter(
        @Qualifier("openaiChatClient") ChatClient openaiClient,
        @Qualifier("alibabaChatClient") ChatClient alibabaClient) {
        
        Map<String, ChatClient> clients = new ConcurrentHashMap<>();
        clients.put("OPENAI", openaiClient);
        clients.put("ALIBABA", alibabaClient);
        
        return new ModelRouter(clients);
    }
} 