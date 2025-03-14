package com.chy.agents.core.chat.adapter;

import com.chy.agents.core.chat.ChatClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring AI ChatClient适配器工厂
 * 用于创建将Spring AI的ChatClient适配为系统内部ChatClient的适配器
 *
 * @author YuRuizhi
 * @date 2025/3/14
 */
@Component
public class SpringAiChatClientFactory {

    /**
     * 创建适配器
     *
     * @param springAiChatClient Spring AI的ChatClient
     * @param provider 提供商名称
     * @param model 模型名称
     * @return 适配后的ChatClient
     */
    public ChatClient createAdapter(org.springframework.ai.chat.client.ChatClient springAiChatClient, 
                                   String provider, 
                                   String model) {
        return createAdapter(springAiChatClient, provider, model, null);
    }
    
    /**
     * 创建适配器
     *
     * @param springAiChatClient Spring AI的ChatClient
     * @param provider 提供商名称
     * @param model 模型名称
     * @param config 配置参数
     * @return 适配后的ChatClient
     */
    public ChatClient createAdapter(org.springframework.ai.chat.client.ChatClient springAiChatClient, 
                                   String provider, 
                                   String model, 
                                   Map<String, Object> config) {
        return new SpringAiChatClientAdapter(springAiChatClient, provider, model, config);
    }
    
    /**
     * 创建OpenAI适配器
     *
     * @param springAiChatClient Spring AI的ChatClient
     * @param model 模型名称
     * @return 适配后的ChatClient
     */
    public ChatClient createOpenAiAdapter(org.springframework.ai.chat.client.ChatClient springAiChatClient, 
                                         String model) {
        Map<String, Object> config = new HashMap<>();
        config.put("temperature", 0.7);
        config.put("topP", 1.0);
        return createAdapter(springAiChatClient, "openai", model, config);
    }
    
    /**
     * 创建Alibaba适配器
     *
     * @param springAiChatClient Spring AI的ChatClient
     * @param model 模型名称
     * @return 适配后的ChatClient
     */
    public ChatClient createAlibabaAdapter(org.springframework.ai.chat.client.ChatClient springAiChatClient, 
                                          String model) {
        Map<String, Object> config = new HashMap<>();
        config.put("temperature", 0.7);
        config.put("topP", 0.8);
        return createAdapter(springAiChatClient, "alibaba", model, config);
    }
} 