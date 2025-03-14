package com.chy.agents.multimodal.image.service;

import com.chy.agents.multimodal.image.model.GenerateImageRequest;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * 图像服务接口
 * 参考Spring AI Alibaba的接口设计
 */
public interface ImageService {
    
    /**
     * 生成图像
     * 
     * @param request 图像生成请求
     * @return 图像数据响应
     */
    ResponseEntity<byte[]> generateImage(GenerateImageRequest request);
    
    /**
     * 根据提示词生成图像
     * 
     * @param prompt 提示词
     * @return 图像生成结果
     */
    ImageGeneration generateImage(String prompt);
    
    /**
     * 根据图像提示生成图像
     * 
     * @param imagePrompt 图像提示
     * @return 图像生成结果
     */
    ImageGeneration generateImage(ImagePrompt imagePrompt);
    
    /**
     * 根据多个图像提示生成图像
     * 
     * @param imagePrompts 多个图像提示
     * @return 图像生成结果列表
     */
    List<ImageGeneration> generateImages(List<ImagePrompt> imagePrompts);
    
    /**
     * 根据图像获取描述
     * 
     * @param imageData 图像数据
     * @return 图像描述
     */
    String describeImage(byte[] imageData);
    
    /**
     * 根据图像获取描述
     * 
     * @param image Spring AI图像对象
     * @return 图像描述
     */
    String describeImage(Image image);
    
    /**
     * 识别图像中的对象
     * 
     * @param imageData 图像数据
     * @return 识别结果
     */
    String detectObjects(byte[] imageData);
    
    /**
     * 识别图像中的对象
     * 
     * @param image Spring AI图像对象
     * @return 识别结果
     */
    String detectObjects(Image image);
} 