package com.chy.agents.core.chat;

import com.chy.agents.core.chat.message.Message;
import com.chy.agents.core.chat.prompt.Prompt;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 聊天客户端接口
 */
public interface ChatClient {
    
    /**
     * 同步调用模型
     *
     * @param prompt 提示信息
     * @return 模型响应
     */
    Message call(Prompt prompt);
    
    /**
     * 异步调用模型
     *
     * @param prompt 提示信息
     * @return 异步响应
     */
    CompletableFuture<Message> callAsync(Prompt prompt);
    
    /**
     * 流式调用模型
     *
     * @param prompt 提示信息
     * @return 消息流
     */
    List<Message> stream(Prompt prompt);
    
    /**
     * 获取模型配置
     *
     * @return 配置参数
     */
    Map<String, Object> getConfig();
    
    /**
     * 获取提供商名称
     *
     * @return 提供商名称
     */
    String getProvider();
    
    /**
     * 获取模型名称
     *
     * @return 模型名称
     */
    String getModel();
} 