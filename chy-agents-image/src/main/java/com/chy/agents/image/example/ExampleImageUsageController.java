package com.chy.agents.image.example;

import com.chy.agents.image.service.ImageAnalysisService;
import com.chy.agents.image.service.ImageGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 示例控制器，展示如何使用图像生成和分析服务
 * 该控制器仅作为示例，不应在生产环境中直接使用
 */
@RestController
@RequestMapping("/examples/image")
public class ExampleImageUsageController {

    private final ImageGenerationService imageGenerationService;
    private final ImageAnalysisService imageAnalysisService;

    @Autowired
    public ExampleImageUsageController(
            ImageGenerationService imageGenerationService,
            ImageAnalysisService imageAnalysisService) {
        this.imageGenerationService = imageGenerationService;
        this.imageAnalysisService = imageAnalysisService;
    }

    /**
     * 简单图像生成示例 - 返回图像文件
     */
    @GetMapping("/generate/simple")
    public ResponseEntity<ByteArrayResource> generateSimpleImage(
            @RequestParam(defaultValue = "一只可爱的猫咪在阳光下玩耍") String prompt) {
        
        byte[] imageData = imageGenerationService.generateImageAsBytes(prompt);
        
        ByteArrayResource resource = new ByteArrayResource(imageData);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=generated-image.png")
                .contentType(MediaType.IMAGE_PNG)
                .contentLength(imageData.length)
                .body(resource);
    }

    /**
     * 高级图像生成示例 - 自定义参数
     */
    @PostMapping("/generate/advanced")
    public ResponseEntity<ByteArrayResource> generateAdvancedImage(
            @RequestBody Map<String, Object> requestBody) {
        
        String prompt = (String) requestBody.getOrDefault("prompt", "未来风格的城市景观");
        int width = (int) requestBody.getOrDefault("width", 1024);
        int height = (int) requestBody.getOrDefault("height", 768);
        String model = (String) requestBody.getOrDefault("model", "stable-diffusion-xl-1024-v1-0");
        
        byte[] imageData = imageGenerationService.generateImageAsBytes(prompt, width, height, model);
        
        ByteArrayResource resource = new ByteArrayResource(imageData);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=advanced-image.png")
                .contentType(MediaType.IMAGE_PNG)
                .contentLength(imageData.length)
                .body(resource);
    }

    /**
     * 图像分析示例 - 上传图像并分析
     */
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, String>> analyzeImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "prompt", defaultValue = "请描述这张图片中的内容") String prompt) throws IOException {
        
        byte[] imageData = file.getBytes();
        String analysisResult = imageAnalysisService.analyzeImage(imageData, prompt);
        
        Map<String, String> response = new HashMap<>();
        response.put("prompt", prompt);
        response.put("analysis", analysisResult);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 综合示例 - 生成图像并分析
     */
    @PostMapping("/generate-and-analyze")
    public ResponseEntity<Map<String, Object>> generateAndAnalyzeImage(
            @RequestParam String generationPrompt,
            @RequestParam(defaultValue = "请详细描述这张图片") String analysisPrompt) {
        
        // 第一步：生成图像
        byte[] imageData = imageGenerationService.generateImageAsBytes(generationPrompt);
        
        // 第二步：分析生成的图像
        String analysisResult = imageAnalysisService.analyzeImage(imageData, analysisPrompt);
        
        // 准备响应
        Map<String, Object> response = new HashMap<>();
        response.put("generationPrompt", generationPrompt);
        response.put("analysisPrompt", analysisPrompt);
        response.put("analysis", analysisResult);
        response.put("imageBase64", Base64.getEncoder().encodeToString(imageData));
        
        return ResponseEntity.ok(response);
    }
} 