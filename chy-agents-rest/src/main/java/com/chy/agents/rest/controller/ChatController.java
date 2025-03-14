package com.chy.agents.rest.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
    
    // 简单的会话管理，生产环境应使用持久化存储
    private final Map<String, List<Message>> sessionStore = new ConcurrentHashMap<>();

    /**
     * 简单聊天接口
     *
     * @param request 包含消息内容的请求
     * @return 聊天响应
     */
    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String userInput = request.get("message");
        
        return Map.of("response", chatClient.prompt()
                .user(userInput)
                .call()
                .content());
    }
    
    /**
     * 带会话历史的聊天接口
     *
     * @param request 包含会话ID和消息内容的请求
     * @return 聊天响应和会话ID
     */
    @PostMapping("/session")
    public Map<String, String> chatWithSession(@RequestBody Map<String, String> request) {
        String sessionId = request.getOrDefault("sessionId", UUID.randomUUID().toString());
        String userInput = request.get("message");
        
        // 获取或创建会话历史
        List<Message> history = sessionStore.computeIfAbsent(sessionId, k -> new ArrayList<>());
        
        // 添加用户消息到历史
        UserMessage userMessage = new UserMessage(userInput);
        history.add(userMessage);
        
        // 使用会话历史调用模型
        String response = chatClient.prompt()
                .messages(history)
                .call()
                .content();
        
        // 将助手回复添加到历史
        history.add(new AssistantMessage(response));
        
        return Map.of(
            "sessionId", sessionId,
            "response", response
        );
    }

    /**
     * 流式聊天接口
     *
     * @param request 包含消息内容的请求
     * @return 流式聊天响应
     */
    @PostMapping("/stream")
    public Flux<Map<String, String>> streamChat(@RequestBody Map<String, String> request) {
        String userInput = request.get("message");
        
        return chatClient.prompt()
                .user(userInput)
                .stream()
                .content()
                .map(content -> Map.of("response", content));
    }
    
    /**
     * 带会话历史的流式聊天接口
     *
     * @param request 包含会话ID和消息内容的请求
     * @return 流式聊天响应
     */
    @PostMapping("/stream/session")
    public Flux<Map<String, String>> streamChatWithSession(@RequestBody Map<String, String> request) {
        String sessionId = request.getOrDefault("sessionId", UUID.randomUUID().toString());
        String userInput = request.get("message");
        
        // 获取或创建会话历史
        List<Message> history = sessionStore.computeIfAbsent(sessionId, k -> new ArrayList<>());
        
        // 添加用户消息到历史
        UserMessage userMessage = new UserMessage(userInput);
        history.add(userMessage);
        
        // 使用会话历史调用模型
        return chatClient.prompt()
                .messages(history)
                .stream()
                .content()
                .map(content -> {
                    Map<String, String> responseMap = new HashMap<>();
                    responseMap.put("sessionId", sessionId);
                    responseMap.put("response", content);
                    return responseMap;
                });
    }
    
    /**
     * 清除会话历史
     *
     * @param sessionId 会话ID
     * @return 操作结果
     */
    @DeleteMapping("/session/{sessionId}")
    public Map<String, String> clearSession(@PathVariable String sessionId) {
        sessionStore.remove(sessionId);
        return Map.of("status", "success", "message", "会话已清除");
    }
} 