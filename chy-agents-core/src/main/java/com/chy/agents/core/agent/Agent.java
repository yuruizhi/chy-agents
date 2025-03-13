package com.chy.agents.core.agent;

import org.springframework.ai.chat.messages.Message;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 代理接口定义
 *
 * @author YuRuizhi
 * @date 2025/3/12
 */
public interface Agent {
    
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
     * 获取代理配置
     *
     * @return 代理配置
     */
    AgentConfig getConfig();
    
    /**
     * 重置代理状态
     */
    void reset();
    
    /**
     * 关闭代理资源
     */
    void close();
    
    /**
     * 获取代理名称
     * 
     * @return 代理名称
     */
    String getName();
    
    /**
     * 设置代理名称
     * 
     * @param name 代理名称
     */
    void setName(String name);
    
    /**
     * 获取代理描述
     * 
     * @return 代理描述
     */
    String getDescription();
    
    /**
     * 设置代理描述
     * 
     * @param description 代理描述
     */
    void setDescription(String description);
    
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