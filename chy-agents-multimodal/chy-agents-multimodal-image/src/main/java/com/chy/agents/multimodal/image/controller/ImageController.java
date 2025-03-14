package com.chy.agents.multimodal.image.controller;

import com.chy.agents.multimodal.image.model.GenerateImageRequest;
import com.chy.agents.multimodal.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 图像处理控制器
 */
@RestController
@RequestMapping("/api/v1/multimodal/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    /**
     * 生成图像
     *
     * @param request 图像生成请求
     * @return 图像数据
     */
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateImage(@RequestBody GenerateImageRequest request) {
        return imageService.generateImage(request);
    }

    /**
     * 分析图像
     *
     * @param file 上传的图像文件
     * @return 图像分析描述
     * @throws IOException 如果文件处理过程中发生错误
     */
    @PostMapping(value = "/describe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String describeImage(@RequestParam("file") MultipartFile file) throws IOException {
        return imageService.describeImage(file.getBytes());
    }

    /**
     * 检测图像中的对象
     *
     * @param file 上传的图像文件
     * @return 对象检测结果
     * @throws IOException 如果文件处理过程中发生错误
     */
    @PostMapping(value = "/detect", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String detectObjects(@RequestParam("file") MultipartFile file) throws IOException {
        return imageService.detectObjects(file.getBytes());
    }
} 