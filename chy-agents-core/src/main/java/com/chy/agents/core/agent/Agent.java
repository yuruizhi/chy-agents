package com.chy.agents.core.agent;

import org.springframework.ai.chat.messages.Message;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 智能代理接口
 * 定义了一个智能代理的基本功能和行为
 */
public interface Agent {
    
    /**
     * 获取代理ID
     *
     * @return 代理ID
     */
    String getId();
    
    /**
     * 获取代理名称
     *
     * @return 代理名称
     */
    String getName();
    
    /**
     * 获取代理描述
     *
     * @return 代理描述
     */
    String getDescription();
    
    /**
     * 获取代理配置
     *
     * @return 代理配置对象
     */
    AgentConfig getConfig();
    
    /**
     * 处理用户输入，生成响应
     *
     * @param input 用户输入内容
     * @return 代理响应内容
     */
    String process(String input);
    
    /**
     * 处理用户输入，附带上下文信息
     *
     * @param input 用户输入内容
     * @param context 上下文信息
     * @return 代理响应内容
     */
    String process(String input, Map<String, Object> context);
    
    /**
     * 获取代理能力列表
     *
     * @return 代理能力列表
     */
    List<String> getCapabilities();
    
    /**
     * 检查代理是否拥有特定能力
     *
     * @param capability 能力名称
     * @return 是否拥有该能力
     */
    boolean hasCapability(String capability);
    
    /**
     * 获取代理状态
     *
     * @return 代理状态
     */
    AgentStatus getStatus();
    
    /**
     * 启动代理
     */
    void start();
    
    /**
     * 停止代理
     */
    void stop();
    
    /**
     * 重置代理状态
     */
    void reset();
    
    /**
     * 同步执行任务
     *
     * @param input 输入参数
     * @param context 上下文参数
     * @return 执行结果
     */
    AgentResponse execute(String input, Map<String, Object> context);
    
    /**
     * 异步执行任务
     *
     * @param input 输入参数
     * @param context 上下文参数
     * @return 异步执行结果
     */
    CompletableFuture<AgentResponse> executeAsync(String input, Map<String, Object> context);
    
    /**
     * 获取代理工具列表
     * 
     * @return 工具列表
     */
    List<Tool> getTools();
    
    /**
     * 设置代理工具列表
     * 
     * @param tools 工具列表
     */
    void setTools(List<Tool> tools);
    
    /**
     * 获取代理记忆
     * 
     * @return 记忆
     */
    Memory getMemory();
    
    /**
     * 设置代理记忆
     * 
     * @param memory 记忆
     */
    void setMemory(Memory memory);
    
    /**
     * 工具接口定义
     */
    interface Tool {
        /**
         * 获取工具名称
         * 
         * @return 工具名称
         */
        String getName();
        
        /**
         * 获取工具描述
         * 
         * @return 工具描述
         */
        String getDescription();
        
        /**
         * 执行工具
         * 
         * @param input 工具输入
         * @return 工具执行结果
         */
        String execute(String input);
    }
    
    /**
     * 记忆接口定义
     */
    interface Memory {
        /**
         * 添加消息
         * 
         * @param message 消息
         */
        void add(Message message);
        
        /**
         * 获取记忆中的消息
         * 
         * @param limit 获取条数限制
         * @return 消息列表
         */
        List<Message> get(int limit);
        
        /**
         * 清空记忆
         */
        void clear();
    }
} 