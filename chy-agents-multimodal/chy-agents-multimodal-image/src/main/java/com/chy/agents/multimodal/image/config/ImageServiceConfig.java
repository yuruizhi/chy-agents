package com.chy.agents.multimodal.image.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 图像服务配置类
 */
@Configuration
@ConfigurationProperties(prefix = "chy.agents.multimodal.image")
public class ImageServiceConfig {
    
    /**
     * 服务是否启用
     */
    private boolean enabled = true;
    
    /**
     * 默认图像生成提供商 (openai, stability, dashscope)
     */
    private String defaultProvider = "openai";
    
    /**
     * 默认图像大小
     */
    private String defaultSize = "1024x1024";
    
    /**
     * 默认模型
     */
    private String defaultModel = "dall-e-3";
    
    // Getters and Setters
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getDefaultProvider() {
        return defaultProvider;
    }
    
    public void setDefaultProvider(String defaultProvider) {
        this.defaultProvider = defaultProvider;
    }
    
    public String getDefaultSize() {
        return defaultSize;
    }
    
    public void setDefaultSize(String defaultSize) {
        this.defaultSize = defaultSize;
    }
    
    public String getDefaultModel() {
        return defaultModel;
    }
    
    public void setDefaultModel(String defaultModel) {
        this.defaultModel = defaultModel;
    }
} 