package com.chy.agents.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;

/**
 * 模型配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "chy.agents.model")
public class ModelConfig {
    
    /**
     * 默认提供商
     */
    private String defaultProvider = "openai";
    
    /**
     * 默认模型
     */
    private String defaultModel = "gpt-4";
    
    /**
     * 默认超时时间（秒）
     */
    private Duration timeout = Duration.ofSeconds(30);
    
    /**
     * 最大重试次数
     */
    private int maxRetries = 3;
    
    /**
     * 默认温度参数
     */
    private float temperature = 0.7f;
    
    /**
     * 提供商配置
     */
    private Map<String, ProviderConfig> providers;
    
    /**
     * 提供商配置
     */
    @Data
    public static class ProviderConfig {
        /**
         * API密钥
         */
        private String apiKey;
        
        /**
         * 端点URL
         */
        private String endpoint;
        
        /**
         * 默认模型
         */
        private String model;
        
        /**
         * 代理配置
         */
        private String proxy;
        
        /**
         * 额外参数
         */
        private Map<String, Object> parameters;
    }
} 