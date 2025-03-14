package com.chy.agents.core.chat.message;

import java.util.Map;

/**
 * 消息接口
 */
public interface Message {
    
    /**
     * 获取消息内容
     *
     * @return 消息内容
     */
    String getContent();
    
    /**
     * 获取消息角色
     *
     * @return 消息角色
     */
    Role getRole();
    
    /**
     * 获取消息元数据
     *
     * @return 元数据
     */
    Map<String, Object> getMetadata();
    
    /**
     * 消息角色枚举
     */
    enum Role {
        SYSTEM,     // 系统消息
        USER,       // 用户消息
        ASSISTANT,  // 助手消息
        FUNCTION    // 函数消息
    }
    
    /**
     * 转换为Spring AI消息
     * 
     * @return Spring AI消息
     */
    default org.springframework.ai.chat.messages.Message toSpringAiMessage() {
        return switch (getRole()) {
            case SYSTEM -> new org.springframework.ai.chat.messages.SystemMessage(getContent());
            case USER -> new org.springframework.ai.chat.messages.UserMessage(getContent());
            case ASSISTANT -> new org.springframework.ai.chat.messages.AssistantMessage(getContent());
            case FUNCTION -> new org.springframework.ai.chat.messages.UserMessage(getContent()); // Spring AI暂无Function类型
        };
    }
    
    /**
     * 从Spring AI消息创建
     * 
     * @param message Spring AI消息
     * @return 内部消息
     */
    static Message fromSpringAiMessage(org.springframework.ai.chat.messages.Message message) {
        if (message instanceof org.springframework.ai.chat.messages.SystemMessage) {
            return BaseMessage.systemMessage(message.getContent());
        } else if (message instanceof org.springframework.ai.chat.messages.UserMessage) {
            return BaseMessage.userMessage(message.getContent());
        } else if (message instanceof org.springframework.ai.chat.messages.AssistantMessage) {
            return BaseMessage.assistantMessage(message.getContent());
        } else {
            return BaseMessage.userMessage(message.getContent());
        }
    }
} 