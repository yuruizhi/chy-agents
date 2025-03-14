package com.chy.agents.multimodal.image.service;

import com.chy.agents.multimodal.image.model.GenerateImageRequest;
import org.springframework.http.ResponseEntity;

/**
 * 图像服务接口
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
     * 根据图像获取描述
     * 
     * @param imageData 图像数据
     * @return 图像描述
     */
    String describeImage(byte[] imageData);
    
    /**
     * 识别图像中的对象
     * 
     * @param imageData 图像数据
     * @return 识别结果
     */
    String detectObjects(byte[] imageData);
} 