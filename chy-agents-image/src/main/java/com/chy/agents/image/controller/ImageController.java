package com.chy.agents.image.controller;

import com.chy.agents.image.config.ImageServiceProperties;
import com.chy.agents.image.service.ImageGenerationService;
import com.chy.agents.image.service.ImageAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 图像处理控制器
 * 提供图像生成和分析的REST API
 *
 * @author YuRuizhi
 */
@RestController
@RequestMapping("/api/image")
public class ImageController {

    private final ImageGenerationService imageGenerationService;
    private final ImageAnalysisService imageAnalysisService;
    private final ImageServiceProperties properties;

    @Autowired
    public ImageController(
            ImageGenerationService imageGenerationService,
            ImageAnalysisService imageAnalysisService,
            ImageServiceProperties properties) {
        this.imageGenerationService = imageGenerationService;
        this.imageAnalysisService = imageAnalysisService;
        this.properties = properties;
    }

    /**
     * 生成图像
     *
     * @param request 图像生成请求
     * @return 生成的图像（Base64编码）
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateImage(@RequestBody ImageGenerationRequest request) {
        try {
            // 应用请求参数或使用默认值
            byte[] imageData = imageGenerationService.generateImageAsBytes(
                request.getPrompt(),
                request.getWidth() > 0 ? request.getWidth() : properties.getWidth(),
                request.getHeight() > 0 ? request.getHeight() : properties.getHeight(),
                properties.getModel()
            );
            
            if (imageData.length == 0) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", "未能生成图像");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("image", Base64.getEncoder().encodeToString(imageData));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * 分析图像
     *
     * @param file 要分析的图像文件
     * @param prompt 分析提示（可选）
     * @return 分析结果
     */
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> analyzeImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "prompt", defaultValue = "请描述这张图片") String prompt) {
        
        try {
            byte[] imageData = file.getBytes();
            String result = imageAnalysisService.analyzeImage(imageData, prompt);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("analysis", result);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "无法读取图像文件: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * 图像生成请求DTO
     */
    public static class ImageGenerationRequest {
        private String prompt;
        private int count = 1;
        private int width = 0;  // 0 means use default from properties
        private int height = 0; // 0 means use default from properties

        // Getters and Setters
        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
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
    }
} 