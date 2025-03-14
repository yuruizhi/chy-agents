package com.chy.agents.image.service;

import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chy.agents.image.config.ImageServiceProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import java.util.stream.Collectors;

/**
 * 图像生成服务
 * 使用Spring AI提供的ImageModel接口生成图像
 *
 * @author YuRuizhi
 */
@Service
public class ImageGenerationService {

    private final ImageModel imageModel;
    private final ImageServiceProperties properties;
    
    @Autowired
    public ImageGenerationService(ImageModel imageModel, ImageServiceProperties properties) {
        this.imageModel = imageModel;
        this.properties = properties;
    }
    
    /**
     * 使用指定提示文本生成图像
     *
     * @param prompt 生成图像的提示文本
     * @return 生成的图像列表
     */
    public List<Image> generateImages(String prompt) {
        return generateImages(prompt, properties.getNumberOfImages(), 
            properties.getWidth(), properties.getHeight(), properties.getModel());
    }
    
    /**
     * 使用指定提示文本和参数生成图像
     *
     * @param prompt 生成图像的提示文本
     * @param numberOfImages 要生成的图像数量
     * @param width 图像宽度
     * @param height 图像高度
     * @param model 使用的模型
     * @return 生成的图像列表
     */
    public List<Image> generateImages(String prompt, int numberOfImages, int width, int height, String model) {
        ImageOptions options = ImageOptionsBuilder.builder()
            .withN(numberOfImages)
            .withWidth(width)
            .withHeight(height)
            .withModel(model)
            .build();
            
        ImagePrompt imagePrompt = new ImagePrompt(prompt, options);
        ImageResponse response = imageModel.call(imagePrompt);
        
        // 从ImageResponse获取所有生成结果
        return response.getResults().stream()
                .map(ImageGeneration::getOutput)
                .collect(Collectors.toList());
    }
    
    /**
     * 生成单张图像并返回byte数组
     *
     * @param prompt 生成图像的提示文本
     * @return 图像数据的byte数组
     */
    public byte[] generateImageAsBytes(String prompt) {
        List<Image> images = generateImages(prompt);
        if (!images.isEmpty()) {
            return getImageBytes(images.get(0));
        }
        return new byte[0];
    }
    
    /**
     * 使用自定义参数生成单张图像并返回byte数组
     *
     * @param prompt 生成图像的提示文本
     * @param width 图像宽度
     * @param height 图像高度
     * @param model 使用的模型
     * @return 图像数据的byte数组
     */
    public byte[] generateImageAsBytes(String prompt, int width, int height, String model) {
        List<Image> images = generateImages(prompt, 1, width, height, model);
        if (!images.isEmpty()) {
            return getImageBytes(images.get(0));
        }
        return new byte[0];
    }
    
    /**
     * 从Image对象获取字节数组
     * 
     * @param image Image对象
     * @return 字节数组
     */
    private byte[] getImageBytes(Image image) {
        // Spring AI 1.0.0-M5版本的Image对象结构
        if (image.getUrl() != null && !image.getUrl().isEmpty()) {
            // URL-based image handling would require a separate HTTP client
            throw new UnsupportedOperationException("暂不支持通过URL获取图像数据");
        }
        
        // 尝试从Base64编码的JSON中获取图像数据
        if (image.getB64Json() != null && !image.getB64Json().isEmpty()) {
            try {
                return Base64.getDecoder().decode(image.getB64Json());
            } catch (Exception e) {
                throw new RuntimeException("无法解码图像数据: " + e.getMessage(), e);
            }
        }
        
        return new byte[0];
    }
} 