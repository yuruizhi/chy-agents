package com.chy.agents.image.service;

import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageClient;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageGenerationService {

    private final ImageClient imageClient;
    
    @Autowired
    public ImageGenerationService(ImageClient imageClient) {
        this.imageClient = imageClient;
    }
    
    public List<Image> generateImages(String prompt, int numberOfImages) {
        ImagePrompt imagePrompt = new ImagePrompt(prompt);
        ImageResponse response = imageClient.call(imagePrompt);
        return response.getResult().getOutput();
    }
    
    public byte[] generateImageAsBytes(String prompt) {
        List<Image> images = generateImages(prompt, 1);
        if (!images.isEmpty()) {
            return images.get(0).getData();
        }
        return new byte[0];
    }
} 