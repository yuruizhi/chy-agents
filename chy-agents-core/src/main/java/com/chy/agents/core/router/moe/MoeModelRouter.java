package com.chy.agents.core.router.moe;

import com.chy.agents.core.agent.Agent;
import com.chy.agents.core.chat.ChatClient;
import com.chy.agents.core.router.ModelRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于专家混合(Mixture of Experts)的模型路由器实现
 * MoE路由通过对输入内容进行分析，选择最适合处理特定任务的"专家"模型
 */
@Service
public class MoeModelRouter implements ModelRouter {

    private static final Logger logger = LoggerFactory.getLogger(MoeModelRouter.class);
    
    // 存储所有可用的模型客户端
    private final Map<String, ChatClient> clientMap = new ConcurrentHashMap<>();
    
    // 存储模型的专长领域配置
    private final Map<String, List<String>> expertiseMap = new HashMap<>();
    
    // 存储模型的优先级
    private final Map<String, Integer> priorityMap = new HashMap<>();
    
    @Autowired
    private MoeRoutingStrategy routingStrategy;
    
    /**
     * 根据提供商名称选择对应的ChatClient
     *
     * @param provider 模型提供商名称
     * @return 对应的ChatClient实例
     */
    @Override
    public ChatClient selectClient(String provider) {
        ChatClient client = clientMap.get(provider);
        if (client == null) {
            logger.warn("未找到提供商 [{}] 的模型客户端，将使用默认模型", provider);
            return getDefaultClient();
        }
        return client;
    }
    
    /**
     * 根据输入内容和上下文智能选择最合适的ChatClient
     *
     * @param input 用户输入内容
     * @return 最合适的ChatClient实例
     */
    @Override
    public ChatClient selectClientByInput(String input) {
        // 通过MoE路由策略选择最合适的模型
        String bestProvider = routingStrategy.selectBestExpert(input, expertiseMap);
        return selectClient(bestProvider);
    }
    
    /**
     * 当主要模型不可用时，提供备选的ChatClient
     *
     * @param provider 原始模型提供商名称
     * @return 备选的ChatClient实例
     */
    @Override
    public ChatClient fallbackClient(String provider) {
        // 根据优先级选择备选模型
        return clientMap.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(provider))
                .sorted((e1, e2) -> priorityMap.getOrDefault(e2.getKey(), 0)
                        .compareTo(priorityMap.getOrDefault(e1.getKey(), 0)))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseGet(this::getDefaultClient);
    }
    
    /**
     * 根据路由策略选择最佳模型客户端
     *
     * @param input 输入内容
     * @param context 上下文参数
     * @return 聊天客户端
     */
    @Override
    public ChatClient selectBestClient(String input, Map<String, Object> context) {
        if (context != null && context.containsKey("forcedProvider")) {
            return selectClient((String) context.get("forcedProvider"));
        }
        return selectClientByInput(input);
    }
    
    /**
     * 获取所有可用的模型客户端
     *
     * @return 客户端列表
     */
    @Override
    public List<ChatClient> getAllClients() {
        return new ArrayList<>(clientMap.values());
    }
    
    /**
     * 获取指定代理的模型客户端
     *
     * @param agent 代理实例
     * @return 聊天客户端
     */
    @Override
    public ChatClient getClientForAgent(Agent agent) {
        // 如果代理配置了特定的模型提供商，则使用该提供商
        if (agent != null && agent.getConfig() != null && agent.getConfig().getModelProvider() != null) {
            return selectClient(agent.getConfig().getModelProvider());
        }
        return getDefaultClient();
    }
    
    /**
     * 注册新的模型客户端
     *
     * @param provider 提供商名称
     * @param client 客户端实例
     */
    @Override
    public void registerClient(String provider, ChatClient client) {
        clientMap.put(provider, client);
        logger.info("已注册提供商 [{}] 的模型客户端", provider);
    }
    
    /**
     * 移除模型客户端
     *
     * @param provider 提供商名称
     */
    @Override
    public void removeClient(String provider) {
        clientMap.remove(provider);
        logger.info("已移除提供商 [{}] 的模型客户端", provider);
    }
    
    /**
     * 设置模型的专长领域
     * 
     * @param provider 提供商名称
     * @param expertises 专长领域列表
     */
    public void setExpertise(String provider, List<String> expertises) {
        expertiseMap.put(provider, expertises);
    }
    
    /**
     * 设置模型的优先级
     * 
     * @param provider 提供商名称
     * @param priority 优先级（数值越大优先级越高）
     */
    public void setPriority(String provider, int priority) {
        priorityMap.put(provider, priority);
    }
    
    /**
     * 获取默认的模型客户端
     * 
     * @return 默认的ChatClient实例
     */
    private ChatClient getDefaultClient() {
        // 返回优先级最高的客户端
        return clientMap.entrySet().stream()
                .sorted((e1, e2) -> priorityMap.getOrDefault(e2.getKey(), 0)
                        .compareTo(priorityMap.getOrDefault(e1.getKey(), 0)))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("没有可用的模型客户端"));
    }
} 