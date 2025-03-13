package com.chy.agents.openai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAI配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "chy.agents.openai")
public class OpenAiConfig {
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * 组织ID
     */
    private String organizationId;
    
    /**
     * 默认模型
     */
    private String model = "gpt-4";
    
    /**
     * 端点URL
     */
    private String endpoint = "https://api.openai.com";
    
    /**
     * 代理配置
     */
    private String proxy;
    
    /**
     * 超时时间（秒）
     */
    private int timeout = 30;
    
    /**
     * 最大重试次数
     */
    private int maxRetries = 3;
    
    /**
     * 温度参数
     */
    private float temperature = 0.7f;
    
    /**
     * 最大Token数
     */
    private int maxTokens = 4096;
} 