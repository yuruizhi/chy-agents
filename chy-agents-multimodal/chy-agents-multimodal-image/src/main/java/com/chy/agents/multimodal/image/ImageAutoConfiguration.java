package com.chy.agents.multimodal.image;

import com.chy.agents.multimodal.image.config.ImageServiceConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * 多模态图像服务自动配置类
 * 
 * @author YuRuizhi
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "chy.agents.multimodal.image", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan("com.chy.agents.multimodal.image")
public class ImageAutoConfiguration {
    // 自动配置类不需要额外内容，通过组件扫描和配置导入完成自动装配
} 