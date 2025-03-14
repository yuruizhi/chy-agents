package com.chy.agents.multimodal.image.model;

import lombok.Data;

/**
 * 图像生成请求
 */
@Data
public class GenerateImageRequest {
    
    /**
     * 提示词
     */
    private String prompt;
    
    /**
     * 图像尺寸
     */
    private String size = "1024x1024";
    
    /**
     * 质量等级
     */
    private String quality = "standard";
    
    /**
     * 风格
     */
    private String style = "vivid";
    
    /**
     * 负面提示词（告诉模型不要包含哪些内容）
     */
    private String negativePrompt;
    
    /**
     * 要使用的图像模型
     */
    private String model = "dall-e-3";
    
    /**
     * 要使用的图像提供商
     */
    private String provider = "openai";
    
    /**
     * 要生成的图像数量
     */
    private int n = 1;
} 