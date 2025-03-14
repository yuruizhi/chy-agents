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
@ConfigurationProperties(prefix = "spring.ai")
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
     * OpenAI配置
     */
    private OpenAiConfig openai;
    
    /**
     * 阿里云配置
     */
    private AlibabaConfig alibaba;
    
    /**
     * 提供商配置
     */
    private Map<String, ProviderConfig> providers;
    
    /**
     * OpenAI配置
     */
    @Data
    public static class OpenAiConfig {
        /**
         * API密钥
         */
        private String apiKey;
        
        /**
         * 聊天模型
         */
        private String chatModel = "gpt-4";
        
        /**
         * 嵌入模型
         */
        private String embeddingModel = "text-embedding-3-small";
        
        /**
         * 图像模型
         */
        private String imageModel = "dall-e-3";
    }
    
    /**
     * 阿里云配置
     */
    @Data
    public static class AlibabaConfig {
        /**
         * 访问密钥ID
         */
        private String accessKeyId;
        
        /**
         * 访问密钥密码
         */
        private String accessKeySecret;
        
        /**
         * 区域
         */
        private String region = "cn-hangzhou";
        
        /**
         * DashScope配置
         */
        private DashScopeConfig dashscope = new DashScopeConfig();
        
        /**
         * DashScope配置
         */
        @Data
        public static class DashScopeConfig {
            /**
             * API密钥
             */
            private String apiKey;
            
            /**
             * 模型
             */
            private String model = "qwen-max";
        }
    }
    
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