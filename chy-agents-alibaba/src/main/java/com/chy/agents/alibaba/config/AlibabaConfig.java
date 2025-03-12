package com.chy.agents.alibaba.config;

import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.ai.alibaba.dashscope")
public class AlibabaConfig {
    private String accessKeyId;
    private String accessKeySecret;
    private String region = "cn-hangzhou";
    private String apiKey;
    private ChatOptions chat = new ChatOptions();

    // Getters and Setters
    
    public static class ChatOptions {
        private String model = "qwen-max";
        private String endpoint;
        
        // Getters and Setters
    }
} 