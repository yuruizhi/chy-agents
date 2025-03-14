package com.chy.agents.multimodal.image;

import com.chy.agents.multimodal.image.config.RestTemplateConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * 图像服务自动配置类
 * 用于自动配置图像服务相关组件
 */
@AutoConfiguration
@ComponentScan(basePackages = "com.chy.agents.multimodal.image")
@Import(RestTemplateConfig.class)
public class ImageAutoConfiguration {
    // 自动配置会自动注册相关组件
} 