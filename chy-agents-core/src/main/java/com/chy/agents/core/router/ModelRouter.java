package com.chy.agents.core.router;

import org.springframework.ai.chat.client.ChatClient;

import java.util.List;
import java.util.Map;

/**
 * 路由请求到不同AI模型的接口
 */
public interface ModelRouter {
    
    /**
     * 根据提供商选择聊天客户端
     *
     * @param provider 提供商名称
     * @return 对应提供商的聊天客户端
     */
    ChatClient selectClient(String provider);
    
    /**
     * 选择默认聊天客户端
     *
     * @return 默认聊天客户端
     */
    ChatClient selectDefaultClient();
    
    /**
     * 获取可用提供商信息的映射
     *
     * @return 提供商信息的映射
     */
    Map<String, Object> getAvailableProviders();
    
    /**
     * 根据输入内容选择最合适的聊天客户端
     *
     * @param input 用户输入内容
     * @return 最合适的聊天客户端
     */
    ChatClient selectClientByInput(String input);
    
    /**
     * 获取所有可用的聊天客户端
     *
     * @return 聊天客户端列表
     */
    List<ChatClient> getAllClients();
    
    /**
     * 注册新的聊天客户端
     *
     * @param provider 提供商名称
     * @param client 聊天客户端
     */
    void registerClient(String provider, ChatClient client);
}