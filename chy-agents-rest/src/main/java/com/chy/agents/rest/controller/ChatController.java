package com.chy.agents.rest.controller;

import com.chy.agents.chat.service.ChatService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.messages.Message;
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

    @Resource
    private ChatService chatService;

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, Object> request) {
        String userInput = (String) request.get("message");
        List<Message> chatHistory = (List<Message>) request.getOrDefault("history", new ArrayList<>());
        
        String response = chatService.chat(userInput, chatHistory);
        
        return Map.of("response", response);
    }

    @GetMapping("/stream")
    public Map<String, Flux<String>> streamChat(@RequestBody Map<String, Object> request) {
        String userInput = (String) request.get("message");
        List<Message> chatHistory = (List<Message>) request.getOrDefault("history", new ArrayList<>());
        Flux<String> resp = chatService.streamChat(userInput, chatHistory);
        return Map.of("response", resp);
    }
} 