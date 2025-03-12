package com.chy.agents.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAi配置。
 *
 * @author YuRuizhi
 * @date 2025/3/12
 */
@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {
    
    private String apiKey;
    private String chatModel = "gpt-3.5-turbo";
    private String embeddingModel = "text-embedding-ada-002";
    private float temperature = 0.7f;
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getChatModel() {
        return chatModel;
    }
    
    public void setChatModel(String chatModel) {
        this.chatModel = chatModel;
    }
    
    public String getEmbeddingModel() {
        return embeddingModel;
    }
    
    public void setEmbeddingModel(String embeddingModel) {
        this.embeddingModel = embeddingModel;
    }
    
    public float getTemperature() {
        return temperature;
    }
    
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
} 