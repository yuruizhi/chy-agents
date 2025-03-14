package com.chy.agents.rest.controller;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 聊天控制器。
 *
 * @author YuRuizhi
 * @date 2025/3/13
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatClient chatClient;

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String userInput = request.get("message");
        
        UserMessage userMessage = new UserMessage(userInput);
        Prompt prompt = new Prompt(userMessage);
        
        ChatResponse response = chatClient.call(prompt);
        String content = response.getResult().getOutput().getContent();
        
        return Map.of("response", content);
    }

    @PostMapping("/stream")
    public Flux<Map<String, String>> streamChat(@RequestBody Map<String, String> request) {
        String userInput = request.get("message");
        
        UserMessage userMessage = new UserMessage(userInput);
        Prompt prompt = new Prompt(userMessage);
        
        return chatClient.stream(prompt)
                .map(response -> Map.of("response", response.getResult().getOutput().getContent()));
    }
} 