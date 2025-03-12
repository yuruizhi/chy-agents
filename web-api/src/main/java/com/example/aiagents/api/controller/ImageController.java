package com.example.aiagents.api.controller;

import com.example.aiagents.image.service.ImageGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/image")
public class ImageController {

    private final ImageGenerationService imageService;
    
    @Autowired
    public ImageController(ImageGenerationService imageService) {
        this.imageService = imageService;
    }
    
    @PostMapping(produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateImage(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        byte[] imageData = imageService.generateImageAsBytes(prompt);
        
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageData);
    }
} 