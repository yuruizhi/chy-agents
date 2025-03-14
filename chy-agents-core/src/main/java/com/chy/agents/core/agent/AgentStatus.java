package com.chy.agents.core.agent;

/**
 * 代理状态枚举
 * 描述代理的当前运行状态
 */
public enum AgentStatus {
    /**
     * 初始化状态
     * 代理已创建但尚未启动
     */
    INITIALIZED,
    
    /**
     * 运行中状态
     * 代理正在正常运行
     */
    RUNNING,
    
    /**
     * 已暂停状态
     * 代理暂时停止处理请求，但可以恢复
     */
    PAUSED,
    
    /**
     * 已停止状态
     * 代理已完全停止，需要重新启动才能使用
     */
    STOPPED,
    
    /**
     * 错误状态
     * 代理遇到错误，需要处理后才能继续使用
     */
    ERROR
} 