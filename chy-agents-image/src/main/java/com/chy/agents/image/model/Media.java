package com.chy.agents.image.model;

import org.springframework.ai.chat.messages.Media;

/**
 * 图像媒体内容类
 * 包含图像的媒体类型和内容
 *
 * @author YuRuizhi
 */
public class Media implements org.springframework.ai.chat.messages.Media {
    
    private final String type;
    private final String data;
    
    /**
     * 构造函数
     *
     * @param type 媒体类型（如 "image/jpeg"）
     * @param data 媒体数据（Base64编码）
     */
    public Media(String type, String data) {
        this.type = type;
        this.data = data;
    }
    
    @Override
    public String getType() {
        return type;
    }
    
    @Override
    public String getData() {
        return data;
    }
} 