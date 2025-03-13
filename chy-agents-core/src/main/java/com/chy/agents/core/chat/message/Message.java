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
} 