package com.chy.agents.image;

import com.chy.agents.image.config.ImageServiceConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * 图像服务自动配置类
 * 
 * @author YuRuizhi
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "chy.agents.image", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import(ImageServiceConfig.class)
@ComponentScan("com.chy.agents.image")
public class ImageAutoConfiguration {
    // 自动配置类不需要额外内容，通过组件扫描和配置导入完成自动装配
} 