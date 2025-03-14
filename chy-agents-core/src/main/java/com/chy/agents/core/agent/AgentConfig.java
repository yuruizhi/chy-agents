package com.chy.agents.core.agent;

import java.util.HashMap;
import java.util.Map;

/**
 * 代理配置类
 * 存储并管理智能代理的各项配置参数
 */
public class AgentConfig {
    
    // 模型提供商
    private String modelProvider;
    
    // 模型名称
    private String modelName;
    
    // 最大token数
    private int maxTokens = 2048;
    
    // 温度参数
    private double temperature = 0.7;
    
    // 系统提示词
    private String systemPrompt;
    
    // 是否启用工具调用
    private boolean enableFunctionCalling = true;
    
    // 是否启用流式响应
    private boolean enableStreaming = false;
    
    // 其他配置参数
    private Map<String, Object> parameters = new HashMap<>();
    
    /**
     * 获取模型提供商
     *
     * @return 模型提供商名称
     */
    public String getModelProvider() {
        return modelProvider;
    }
    
    /**
     * 设置模型提供商
     *
     * @param modelProvider 模型提供商名称
     */
    public void setModelProvider(String modelProvider) {
        this.modelProvider = modelProvider;
    }
    
    /**
     * 获取模型名称
     *
     * @return 模型名称
     */
    public String getModelName() {
        return modelName;
    }
    
    /**
     * 设置模型名称
     *
     * @param modelName 模型名称
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    
    /**
     * 获取最大token数
     *
     * @return 最大token数
     */
    public int getMaxTokens() {
        return maxTokens;
    }
    
    /**
     * 设置最大token数
     *
     * @param maxTokens 最大token数
     */
    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }
    
    /**
     * 获取温度参数
     *
     * @return 温度参数
     */
    public double getTemperature() {
        return temperature;
    }
    
    /**
     * 设置温度参数
     *
     * @param temperature 温度参数
     */
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    
    /**
     * 获取系统提示词
     *
     * @return 系统提示词
     */
    public String getSystemPrompt() {
        return systemPrompt;
    }
    
    /**
     * 设置系统提示词
     *
     * @param systemPrompt 系统提示词
     */
    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
    
    /**
     * 是否启用工具调用
     *
     * @return 是否启用工具调用
     */
    public boolean isEnableFunctionCalling() {
        return enableFunctionCalling;
    }
    
    /**
     * 设置是否启用工具调用
     *
     * @param enableFunctionCalling 是否启用工具调用
     */
    public void setEnableFunctionCalling(boolean enableFunctionCalling) {
        this.enableFunctionCalling = enableFunctionCalling;
    }
    
    /**
     * 是否启用流式响应
     *
     * @return 是否启用流式响应
     */
    public boolean isEnableStreaming() {
        return enableStreaming;
    }
    
    /**
     * 设置是否启用流式响应
     *
     * @param enableStreaming 是否启用流式响应
     */
    public void setEnableStreaming(boolean enableStreaming) {
        this.enableStreaming = enableStreaming;
    }
    
    /**
     * 获取其他配置参数
     *
     * @return 配置参数映射
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    /**
     * 设置其他配置参数
     *
     * @param parameters 配置参数映射
     */
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    /**
     * 获取特定参数值
     *
     * @param key 参数名
     * @return 参数值
     */
    public Object getParameter(String key) {
        return parameters.get(key);
    }
    
    /**
     * 设置特定参数值
     *
     * @param key 参数名
     * @param value 参数值
     */
    public void setParameter(String key, Object value) {
        parameters.put(key, value);
    }
} 