package com.chy.agents.model.alibaba.config;

import com.alibaba.cloud.ai.dashscope.DashScopeChatClient;
import com.alibaba.cloud.ai.dashscope.DashScopeChatOptions;
import com.chy.agents.common.config.ModelConfig;
import org.springframework.ai.chat.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 阿里云模型配置类
 */
@Configuration
public class AlibabaModelConfig {

    /**
     * 创建DashScope聊天客户端
     *
     * @param modelConfig 模型配置
     * @return 聊天客户端
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring.ai.alibaba.dashscope", name = "api-key")
    @Primary
    public ChatClient dashScopeChatClient(ModelConfig modelConfig) {
        // 创建DashScopeChatOptions实例，并设置模型
        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .withModel(modelConfig.getAlibaba().getDashscope().getModel())
                .build();
        
        // 使用DashScopeChatOptions创建DashScopeChatClient实例
        return new DashScopeChatClient(modelConfig.getAlibaba().getDashscope().getApiKey(), options);
    }
} 