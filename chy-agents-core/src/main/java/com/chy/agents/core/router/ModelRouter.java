package com.chy.agents.core.router;

import com.chy.agents.core.agent.Agent;
import com.chy.agents.core.chat.ChatClient;

import java.util.List;
import java.util.Map;

/**
 * 模型路由器接口，定义模型选择和路由的基本功能
 */
public interface ModelRouter {
    
    /**
     * 根据提供商名称选择对应的ChatClient
     *
     * @param provider 模型提供商名称
     * @return 对应的ChatClient实例
     */
    ChatClient selectClient(String provider);
    
    /**
     * 根据输入内容和上下文智能选择最合适的ChatClient
     *
     * @param input 用户输入内容
     * @return 最合适的ChatClient实例
     */
    ChatClient selectClientByInput(String input);
    
    /**
     * 当主要模型不可用时，提供备选的ChatClient
     *
     * @param provider 原始模型提供商名称
     * @return 备选的ChatClient实例
     */
    ChatClient fallbackClient(String provider);
    
    /**
     * 根据路由策略选择最佳模型客户端
     *
     * @param input 输入内容
     * @param context 上下文参数
     * @return 聊天客户端
     */
    ChatClient selectBestClient(String input, Map<String, Object> context);
    
    /**
     * 获取所有可用的模型客户端
     *
     * @return 客户端列表
     */
    List<ChatClient> getAllClients();
    
    /**
     * 获取指定代理的模型客户端
     *
     * @param agent 代理实例
     * @return 聊天客户端
     */
    ChatClient getClientForAgent(Agent agent);
    
    /**
     * 注册新的模型客户端
     *
     * @param provider 提供商名称
     * @param client 客户端实例
     */
    void registerClient(String provider, ChatClient client);
    
    /**
     * 移除模型客户端
     *
     * @param provider 提供商名称
     */
    void removeClient(String provider);
} 