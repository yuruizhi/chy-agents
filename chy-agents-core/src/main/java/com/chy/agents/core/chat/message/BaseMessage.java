package com.chy.agents.core.chat.message;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 基础消息实现
 */
@Data
@Builder
public class BaseMessage implements Message {
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息角色
     */
    private Role role;
    
    /**
     * 消息元数据
     */
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
    
    /**
     * 创建系统消息
     *
     * @param content 消息内容
     * @return 系统消息
     */
    public static BaseMessage systemMessage(String content) {
        return BaseMessage.builder()
                .content(content)
                .role(Role.SYSTEM)
                .build();
    }
    
    /**
     * 创建用户消息
     *
     * @param content 消息内容
     * @return 用户消息
     */
    public static BaseMessage userMessage(String content) {
        return BaseMessage.builder()
                .content(content)
                .role(Role.USER)
                .build();
    }
    
    /**
     * 创建助手消息
     *
     * @param content 消息内容
     * @return 助手消息
     */
    public static BaseMessage assistantMessage(String content) {
        return BaseMessage.builder()
                .content(content)
                .role(Role.ASSISTANT)
                .build();
    }
    
    /**
     * 创建函数消息
     *
     * @param content 消息内容
     * @return 函数消息
     */
    public static BaseMessage functionMessage(String content) {
        return BaseMessage.builder()
                .content(content)
                .role(Role.FUNCTION)
                .build();
    }
} 