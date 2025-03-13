package com.chy.agents.alibaba.config;

import com.alibaba.cloud.ai.config.AlibabaAiProperties;
import com.alibaba.cloud.ai.llm.AlibabaLlmTemplate;
import org.springframework.ai.chat.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlibabaChatClientConfiguration {

    @Bean
    public AlibabaAiProperties alibabaAiProperties(AlibabaConfig config) {
        AlibabaAiProperties properties = new AlibabaAiProperties();
        properties.setApiKey(config.getApiKey());
        properties.setModel(config.getModel());
        properties.setEndpoint(config.getEndpoint());
        return properties;
    }

    @Bean
    public ChatClient alibabaChatClient(AlibabaAiProperties properties) {
        return new AlibabaLlmTemplate(properties);
    }
} 