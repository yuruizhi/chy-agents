package com.chy.agents.core.router;

import com.chy.agents.core.agent.Agent;
import com.chy.agents.core.chat.ChatClient;

import java.util.List;
import java.util.Map;

/**
 * 模型路由接口
 */
public interface ModelRouter {
    
    /**
     * 根据提供商选择模型客户端
     *
     * @param provider 提供商名称
     * @return 聊天客户端
     */
    ChatClient selectClient(String provider);
    
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