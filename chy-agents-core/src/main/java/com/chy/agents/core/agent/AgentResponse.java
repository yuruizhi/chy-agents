package com.chy.agents.core.agent;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 代理响应结果
 */
@Data
@Builder
public class AgentResponse {
    
    /**
     * 响应内容
     */
    private String content;
    
    /**
     * 响应类型
     */
    private ResponseType type;
    
    /**
     * 执行状态
     */
    private ExecutionStatus status;
    
    /**
     * 错误信息
     */
    private String error;
    
    /**
     * 额外数据
     */
    private Map<String, Object> metadata;
    
    /**
     * 响应类型枚举
     */
    public enum ResponseType {
        TEXT,           // 文本响应
        IMAGE,          // 图片响应
        AUDIO,          // 音频响应
        VIDEO,          // 视频响应
        FUNCTION_CALL,  // 函数调用
        ERROR           // 错误响应
    }
    
    /**
     * 执行状态枚举
     */
    public enum ExecutionStatus {
        SUCCESS,        // 执行成功
        FAILED,         // 执行失败
        TIMEOUT,        // 执行超时
        INTERRUPTED     // 执行中断
    }
} 