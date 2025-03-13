package com.chy.agents.core.agent;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.util.Map;

/**
 * 代理配置
 */
@Data
@Builder
public class AgentConfig {
    
    /**
     * 代理ID
     */
    private String id;
    
    /**
     * 代理名称
     */
    private String name;
    
    /**
     * 代理描述
     */
    private String description;
    
    /**
     * 模型提供商
     */
    private String provider;
    
    /**
     * 模型名称
     */
    private String model;
    
    /**
     * 超时时间
     */
    @Builder.Default
    private Duration timeout = Duration.ofSeconds(30);
    
    /**
     * 最大重试次数
     */
    private int maxRetries;
    
    /**
     * 温度参数 (0.0-2.0)
     */
    private float temperature;
    
    /**
     * 系统提示词
     */
    private String systemPrompt;
    
    /**
     * 模型配置
     */
    @Builder.Default
    private Map<String, Object> modelConfig = Map.of();
    
    /**
     * 其他配置参数
     */
    @Builder.Default
    private Map<String, Object> parameters = Map.of();
    
    /**
     * 构建默认配置
     */
    public static AgentConfig getDefault() {
        return AgentConfig.builder()
                .id("default")
                .name("Default Agent")
                .timeout(Duration.ofSeconds(30))
                .maxRetries(3)
                .temperature(0.7f)
                .build();
    }
} 