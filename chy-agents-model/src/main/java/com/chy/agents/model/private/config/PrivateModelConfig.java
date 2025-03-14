package com.chy.agents.model.private.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.Map;

/**
 * 私有模型配置
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "chy.agents.private")
public class PrivateModelConfig {
    
    /**
     * 是否启用私有模型
     */
    private boolean enabled = false;
    
    /**
     * 默认模型类型
     */
    private ModelType modelType = ModelType.LLAMA;
    
    /**
     * 默认模型
     */
    private String model = "llama2-7b";
    
    /**
     * 端点URL (http://localhost:8000 for API server)
     */
    @NotBlank(message = "Endpoint must not be blank")
    private String endpoint = "http://localhost:8000";
    
    /**
     * 超时时间（秒）
     */
    @Positive(message = "Timeout must be positive")
    private int timeout = 60;
    
    /**
     * 温度参数
     */
    private float temperature = 0.7f;
    
    /**
     * 最大Token数
     */
    @Positive(message = "Max tokens must be positive")
    private int maxTokens = 2048;
    
    /**
     * 额外参数
     */
    private Map<String, Object> parameters;
    
    /**
     * 模型类型枚举
     */
    public enum ModelType {
        LLAMA,     // llama.cpp
        VLLM,      // vLLM
        GGML,      // GGML
        OLLAMA,    // Ollama
        CUSTOM     // 自定义
    }
} 