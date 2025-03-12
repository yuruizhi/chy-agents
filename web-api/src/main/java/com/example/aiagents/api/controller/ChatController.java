package com.example.aiagents.api.controller;

import com.example.aiagents.chat.service.ChatService;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    
    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    
    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, Object> request) {
        String userInput = (String) request.get("message");
        List<Message> chatHistory = (List<Message>) request.getOrDefault("history", new ArrayList<>());
        
        String response = chatService.chat(userInput, chatHistory);
        
        return Map.of("response", response);
    }
} 