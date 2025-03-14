package com.chy.agents.core.router;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模型路由器接口的简单实现
 */
@Service
@Slf4j
public class SimpleModelRouter implements ModelRouter {

    private final Map<String, ChatClient> clientMap = new ConcurrentHashMap<>();
    
    @Autowired(required = false)
    @Qualifier("openAiChatClient")
    private ChatClient openAiChatClient;
    
    @Autowired(required = false)
    @Qualifier("dashscopeChatClient")
    private ChatClient dashscopeChatClient;
    
    /**
     * 初始化客户端映射
     */
    @Autowired
    public void init() {
        if (openAiChatClient != null) {
            clientMap.put("openai", openAiChatClient);
            log.info("已注册OpenAI聊天客户端");
        }
        
        if (dashscopeChatClient != null) {
            clientMap.put("dashscope", dashscopeChatClient);
            log.info("已注册Dashscope聊天客户端");
        }
    }

    @Override
    public ChatClient selectClient(String provider) {
        ChatClient client = clientMap.get(provider.toLowerCase());
        if (client == null) {
            log.warn("未找到提供商: {}的客户端。使用默认客户端。", provider);
            return selectDefaultClient();
        }
        return client;
    }

    @Override
    public ChatClient selectDefaultClient() {
        if (openAiChatClient != null) {
            return openAiChatClient;
        } else if (dashscopeChatClient != null) {
            return dashscopeChatClient;
        } else {
            throw new IllegalStateException("没有可用的聊天客户端");
        }
    }

    @Override
    public Map<String, Object> getAvailableProviders() {
        Map<String, Object> result = new HashMap<>();
        clientMap.forEach((key, value) -> {
            Map<String, Object> providerInfo = new HashMap<>();
            providerInfo.put("available", true);
            providerInfo.put("type", value.getClass().getSimpleName());
            result.put(key, providerInfo);
        });
        return result;
    }
    
    @Override
    public ChatClient selectClientByInput(String input) {
        // 简单实现，直接返回默认客户端
        // 更复杂的实现可以根据输入内容分析选择合适的模型
        return selectDefaultClient();
    }
    
    @Override
    public List<ChatClient> getAllClients() {
        return new ArrayList<>(clientMap.values());
    }
    
    @Override
    public void registerClient(String provider, ChatClient client) {
        clientMap.put(provider.toLowerCase(), client);
        log.info("已注册提供商: {}的客户端", provider);
    }
}