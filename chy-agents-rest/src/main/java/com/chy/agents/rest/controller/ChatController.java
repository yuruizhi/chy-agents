package com.chy.agents.rest.controller;

import com.chy.agents.chat.service.ChatService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Resource
    private ChatService chatService;

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, Object> request) {
        String userInput = (String) request.get("message");
        List<Message> chatHistory = (List<Message>) request.getOrDefault("history", new ArrayList<>());
        
        String response = chatService.chat(userInput, chatHistory);
        
        return Map.of("response", response);
    }
} 