package com.chy.agents.core.router.spring;

import com.chy.agents.core.agent.Agent;
import com.chy.agents.core.chat.ChatClient;
import com.chy.agents.core.chat.adapter.SpringAiChatClientAdapter;
import com.chy.agents.core.router.ModelRouter;
import com.chy.agents.core.router.moe.MoeRoutingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient.MetadataMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 基于Spring AI的模型路由器
 * 支持混合模型路由和动态选择
 * 
 * @author YuRuizhi
 * @date 2025/06/19
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "chy.agents.router", name = "type", havingValue = "spring")
public class SpringAiModelRouter implements ModelRouter {

    // 存储所有可用的Spring AI ChatClient
    private final Map<String, org.springframework.ai.chat.client.ChatClient> springAiClients = new ConcurrentHashMap<>();
    
    // 存储适配后的项目ChatClient
    private final Map<String, ChatClient> adaptedClients = new ConcurrentHashMap<>();
    
    // 存储模型的专长领域配置
    private final Map<String, List<String>> expertiseMap = new HashMap<>();
    
    // 存储模型的优先级
    private final Map<String, Integer> priorityMap = new HashMap<>();
    
    // 适配器创建函数
    private final Function<org.springframework.ai.chat.client.ChatClient, ChatClient> adapterFactory;
    
    @Autowired(required = false)
    private MoeRoutingStrategy routingStrategy;
    
    public SpringAiModelRouter(Function<org.springframework.ai.chat.client.ChatClient, ChatClient> adapterFactory) {
        this.adapterFactory = adapterFactory;
    }
    
    /**
     * 注册Spring AI模型客户端
     * 
     * @param provider 提供商名称
     * @param client Spring AI的ChatClient
     */
    public void registerSpringAiClient(String provider, org.springframework.ai.chat.client.ChatClient client) {
        springAiClients.put(provider, client);
        // 自动创建适配的客户端
        ChatClient adaptedClient = adapterFactory.apply(client);
        adaptedClients.put(provider, adaptedClient);
        log.info("已注册Spring AI提供商 [{}] 的模型客户端", provider);
    }
    
    @Override
    public ChatClient selectClient(String provider) {
        ChatClient client = adaptedClients.get(provider);
        if (client == null) {
            log.warn("未找到提供商 [{}] 的模型客户端，将使用默认模型", provider);
            return getDefaultClient();
        }
        return client;
    }
    
    @Override
    public ChatClient selectClientByInput(String input) {
        if (routingStrategy != null) {
            // 使用MoE路由策略
            String bestProvider = routingStrategy.selectBestExpert(input, expertiseMap);
            return selectClient(bestProvider);
        } else {
            // 如果没有策略，返回默认
            return getDefaultClient();
        }
    }
    
    @Override
    public ChatClient fallbackClient(String provider) {
        // 根据优先级选择备选模型
        return adaptedClients.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(provider))
                .sorted((e1, e2) -> priorityMap.getOrDefault(e2.getKey(), 0)
                        .compareTo(priorityMap.getOrDefault(e1.getKey(), 0)))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseGet(this::getDefaultClient);
    }
    
    @Override
    public ChatClient selectBestClient(String input, Map<String, Object> context) {
        if (context != null && context.containsKey("forcedProvider")) {
            return selectClient((String) context.get("forcedProvider"));
        }
        
        // 使用Spring AI元数据功能增强选择
        if (context != null && context.containsKey("requirement")) {
            String requirement = (String) context.get("requirement");
            
            // 查找最适合需求的模型
            Map<String, org.springframework.ai.chat.client.ChatClient> filteredClients =
                    findBestModelsForRequirement(requirement);
            
            if (!filteredClients.isEmpty()) {
                String bestProvider = filteredClients.keySet().iterator().next();
                return adaptedClients.get(bestProvider);
            }
        }
        
        return selectClientByInput(input);
    }
    
    /**
     * 根据需求选择最佳模型
     */
    private Map<String, org.springframework.ai.chat.client.ChatClient> findBestModelsForRequirement(String requirement) {
        // 从Spring AI客户端中检索元数据进行智能选择
        return springAiClients.entrySet().stream()
                .filter(entry -> {
                    try {
                        Map<String, Object> metadata = entry.getValue().metadata(MetadataMode.FULL);
                        
                        // 根据能力匹配模型
                        if (metadata.containsKey("capabilities")) {
                            @SuppressWarnings("unchecked")
                            List<String> capabilities = (List<String>) metadata.get("capabilities");
                            
                            // 检查是否有匹配的能力
                            if (requirement.contains("code") && capabilities.contains("code")) {
                                return true;
                            } else if (requirement.contains("vision") && capabilities.contains("vision")) {
                                return true;
                            } else if (requirement.contains("math") && capabilities.contains("math")) {
                                return true;
                            } else if (requirement.contains("reasoning") && capabilities.contains("reasoning")) {
                                return true;
                            }
                        }
                        return false;
                    } catch (Exception e) {
                        log.warn("获取模型元数据失败: {}", e.getMessage());
                        return false;
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    @Override
    public List<ChatClient> getAllClients() {
        return new ArrayList<>(adaptedClients.values());
    }
    
    @Override
    public ChatClient getClientForAgent(Agent agent) {
        // 如果代理配置了特定的模型提供商，则使用该提供商
        if (agent != null && agent.getConfig() != null && agent.getConfig().getModelProvider() != null) {
            return selectClient(agent.getConfig().getModelProvider());
        }
        return getDefaultClient();
    }
    
    @Override
    public void registerClient(String provider, ChatClient client) {
        adaptedClients.put(provider, client);
        log.info("已注册提供商 [{}] 的适配模型客户端", provider);
    }
    
    @Override
    public void removeClient(String provider) {
        adaptedClients.remove(provider);
        springAiClients.remove(provider);
        log.info("已移除提供商 [{}] 的模型客户端", provider);
    }
    
    /**
     * 设置模型的专长领域
     */
    public void setExpertise(String provider, List<String> expertises) {
        expertiseMap.put(provider, expertises);
    }
    
    /**
     * 设置模型的优先级
     */
    public void setPriority(String provider, int priority) {
        priorityMap.put(provider, priority);
    }
    
    /**
     * 获取默认的模型客户端
     */
    private ChatClient getDefaultClient() {
        // 返回优先级最高的客户端
        return adaptedClients.entrySet().stream()
                .sorted((e1, e2) -> priorityMap.getOrDefault(e2.getKey(), 0)
                        .compareTo(priorityMap.getOrDefault(e1.getKey(), 0)))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("没有可用的模型客户端"));
    }
}