package com.chy.agents.core.router;

import org.springframework.ai.chat.client.ChatClient;

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
}