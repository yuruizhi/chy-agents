package com.chy.agents.alibaba.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * 阿里云通义配置
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "chy.agents.alibaba")
public class AlibabaConfig {
    
    /**
     * API密钥
     */
    @NotBlank(message = "API key must not be blank")
    private String apiKey;
    
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
     * 默认模型
     */
    private String model = "qwen-max";
    
    /**
     * 端点URL
     */
    private String endpoint = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
    
    /**
     * 代理配置
     */
    private String proxy;
    
    /**
     * 超时时间（秒）
     */
    @Positive(message = "Timeout must be positive")
    private int timeout = 30;
    
    /**
     * 最大重试次数
     */
    @Min(value = 0, message = "Max retries must not be negative")
    private int maxRetries = 3;
    
    /**
     * 温度参数
     */
    @Min(value = 0, message = "Temperature must be between 0 and 1")
    @Max(value = 1, message = "Temperature must be between 0 and 1")
    private float temperature = 0.7f;
    
    /**
     * 最大Token数
     */
    @Positive(message = "Max tokens must be positive")
    private int maxTokens = 4096;
    
    /**
     * 验证配置
     */
    public void validate() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key must not be blank");
        }
        
        if (temperature < 0 || temperature > 1) {
            throw new IllegalArgumentException("Temperature must be between 0 and 1");
        }
        
        if (maxTokens <= 0) {
            throw new IllegalArgumentException("Max tokens must be positive");
        }
        
        if (timeout <= 0) {
            throw new IllegalArgumentException("Timeout must be positive");
        }
        
        if (maxRetries < 0) {
            throw new IllegalArgumentException("Max retries must not be negative");
        }
    }
} 