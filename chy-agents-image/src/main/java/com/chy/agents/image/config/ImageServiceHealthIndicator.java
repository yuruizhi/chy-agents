package com.chy.agents.image.config;

import org.springframework.ai.image.ImageModel;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * 图像服务健康检查指示器
 *
 * @author YuRuizhi
 */
public class ImageServiceHealthIndicator implements HealthIndicator {

    private final ImageModel imageModel;

    public ImageServiceHealthIndicator(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    @Override
    public Health health() {
        try {
            // 尝试执行一个简单的健康检查操作
            // 对于稳定性和生产环境，可能需要实现一个更轻量级的健康检查方法
            boolean isAvailable = imageModel != null;
            
            if (isAvailable) {
                return Health.up()
                        .withDetail("status", "可用")
                        .withDetail("provider", imageModel.getClass().getSimpleName())
                        .build();
            } else {
                return Health.down()
                        .withDetail("status", "不可用")
                        .withDetail("reason", "图像模型为空")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("status", "不可用")
                    .withDetail("reason", e.getMessage())
                    .build();
        }
    }
} 