package com.chy.agents.image.config;

import org.springframework.ai.image.ImageModel;
import org.springframework.ai.stabilityai.StabilityAiImageModel;
import org.springframework.ai.stabilityai.api.StabilityAiApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 图像服务配置类
 *
 * @author YuRuizhi
 */
@Configuration
@EnableConfigurationProperties(ImageServiceProperties.class)
public class ImageServiceConfig {

    /**
     * 配置图像生成客户端，如果未特别指定使用的客户端，则使用StabilityAI
     */
    @Bean
    @ConditionalOnMissingBean(ImageModel.class)
    @ConditionalOnProperty(prefix = "chy.agents.image", name = "enabled", havingValue = "true", matchIfMissing = true)
    public ImageModel defaultImageModel(ImageServiceProperties properties) {
        // 如果未特别配置，默认使用StabilityAI
        if (properties.getProvider().equalsIgnoreCase("stabilityai")) {
            StabilityAiApi stabilityAiApi = new StabilityAiApi(properties.getApiKey());
            return new StabilityAiImageModel(stabilityAiApi);
        }
        
        // 可以在这里添加其他图像生成提供商的支持
        throw new IllegalArgumentException("不支持的图像生成提供商: " + properties.getProvider());
    }
    
    /**
     * 图像服务健康检查
     */
    @Bean
    public ImageServiceHealthIndicator imageServiceHealthIndicator(ImageModel imageModel) {
        return new ImageServiceHealthIndicator(imageModel);
    }
} 