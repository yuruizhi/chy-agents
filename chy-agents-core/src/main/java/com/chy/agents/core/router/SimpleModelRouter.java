package com.chy.agents.core.router;

import com.chy.agents.core.agent.Agent;
import com.chy.agents.core.chat.ChatClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单模型路由实现
 */
@Slf4j
public class SimpleModelRouter implements ModelRouter {
    
    private final Map<String, ChatClient> clients = new ConcurrentHashMap<>();
    private final String defaultProvider;
    
    public SimpleModelRouter(String defaultProvider) {
        this.defaultProvider = defaultProvider;
    }
    
    @Override
    public ChatClient selectClient(String provider) {
        ChatClient client = clients.get(provider);
        if (client == null) {
            throw new IllegalArgumentException("No client found for provider: " + provider);
        }
        return client;
    }
    
    @Override
    public ChatClient selectBestClient(String input, Map<String, Object> context) {
        // 当前简单实现：返回默认提供商的客户端
        // TODO: 实现更智能的路由策略
        return selectClient(defaultProvider);
    }
    
    @Override
    public List<ChatClient> getAllClients() {
        return new ArrayList<>(clients.values());
    }
    
    @Override
    public ChatClient getClientForAgent(Agent agent) {
        String provider = (String) agent.getConfig().getModelConfig().get("provider");
        return selectClient(provider != null ? provider : defaultProvider);
    }
    
    @Override
    public void registerClient(String provider, ChatClient client) {
        Assert.hasText(provider, "Provider must not be empty");
        Assert.notNull(client, "Client must not be null");
        
        clients.put(provider, client);
        log.info("Registered client for provider: {}", provider);
    }
    
    @Override
    public void removeClient(String provider) {
        Assert.hasText(provider, "Provider must not be empty");
        
        ChatClient removed = clients.remove(provider);
        if (removed != null) {
            log.info("Removed client for provider: {}", provider);
        }
    }
} 