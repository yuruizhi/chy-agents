package com.chy.agents.multimodal.image.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图像生成请求
 * 参考Spring AI Alibaba的实现进行扩展
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
     * 支持：openai, dashscope, stability-ai
     */
    private String provider = "openai";
    
    /**
     * 要生成的图像数量
     */
    private int n = 1;
    
    /**
     * 相似度约束（1.0 表示生成与描述极为相似的图像）
     * 仅在某些模型中支持
     */
    private Float similarityBoost;
    
    /**
     * 随机种子
     * 用于保证图像生成的可重复性
     */
    private Long seed;
    
    /**
     * 输出图像格式
     * 例如："png"、"jpg"、"webp"
     */
    private String responseFormat;
} 