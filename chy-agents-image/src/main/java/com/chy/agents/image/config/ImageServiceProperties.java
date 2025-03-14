package com.chy.agents.image.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 图像服务配置属性
 *
 * @author YuRuizhi
 */
@ConfigurationProperties("chy.agents.image")
public class ImageServiceProperties {

    /**
     * 是否启用图像服务
     */
    private boolean enabled = true;
    
    /**
     * 图像生成提供商
     */
    private String provider = "stabilityai";
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * 默认图像宽度
     */
    private int width = 1024;
    
    /**
     * 默认图像高度
     */
    private int height = 1024;
    
    /**
     * 默认图像生成数量
     */
    private int numberOfImages = 1;
    
    /**
     * 默认模型
     */
    private String model = "stable-diffusion-xl-1024-v1-0";
    
    /**
     * 创新度
     */
    private double creativity = 0.7;

    // Getters and Setters
    
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getNumberOfImages() {
        return numberOfImages;
    }

    public void setNumberOfImages(int numberOfImages) {
        this.numberOfImages = numberOfImages;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getCreativity() {
        return creativity;
    }

    public void setCreativity(double creativity) {
        this.creativity = creativity;
    }
} 