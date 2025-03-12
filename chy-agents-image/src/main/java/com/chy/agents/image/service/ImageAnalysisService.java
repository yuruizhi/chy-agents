package com.chy.agents.image.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Base64;

@Service
public class ImageAnalysisService {

    private final ChatClient visionChatClient; // 支持视觉能力的ChatClient
    
    @Autowired
    public ImageAnalysisService(ChatClient visionChatClient) {
        this.visionChatClient = visionChatClient;
    }
    
    public String analyzeImage(byte[] imageData, String prompt) {
        String base64Image = Base64.getEncoder().encodeToString(imageData);
        Media imageMedia = new Media("image/jpeg", base64Image);
        UserMessage userMessage = new UserMessage(prompt, List.of(imageMedia));
        
        Prompt imagePrompt = new Prompt(List.of(userMessage));
        return visionChatClient.call(imagePrompt).getResult().getOutput().getContent();
    }
} 