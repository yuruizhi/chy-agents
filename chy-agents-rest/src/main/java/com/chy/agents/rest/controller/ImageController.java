package com.chy.agents.rest.controller;

import com.chy.agents.image.service.ImageGenerationService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/image")
public class ImageController {

    @Resource
    private ImageGenerationService imageService;

    @PostMapping(produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateImage(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        byte[] imageData = imageService.generateImageAsBytes(prompt);
        
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageData);
    }
} 