package com.chy.agents.rest.dto;

import lombok.Data;

/**
 * 聊天请求DTO
 *
 * @author YuRuizhi
 * @date 2025/3/12
 */
@Data
public class ChatRequest {
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户消息
     */
    private String message;
    
    /**
     * 提供商（可选）
     */
    private String provider;
} 