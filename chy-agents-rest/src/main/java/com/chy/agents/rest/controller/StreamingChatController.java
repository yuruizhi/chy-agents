package com.chy.agents.rest.controller;

import com.chy.agents.chat.service.StreamingChatService;
import com.chy.agents.rest.dto.ChatRequest;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流式聊天控制器
 *
 * @author YuRuizhi
 * @date 2025/3/12
 */
@Controller
@CrossOrigin
public class StreamingChatController {

    @Resource
    private StreamingChatService streamingChatService;

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    // 存储会话历史
    private final Map<String, List<Message>> sessionStore = new ConcurrentHashMap<>();

    /**
     * 流式聊天
     *
     * @param request 聊天请求
     */
    @MessageMapping("/chat.stream")
    public void streamChat(@Payload ChatRequest request) {
        String sessionId = request.getSessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }

        // 获取或创建会话历史
        List<Message> history = sessionStore.computeIfAbsent(sessionId, k -> new ArrayList<>());
        
        // 添加用户消息到历史
        String userInput = request.getMessage();
        
        // 发送确认消息
        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, 
                Map.of("type", "confirm", "sessionId", sessionId, "message", "开始处理请求..."));
        
        // 获取流式响应
        Flux<String> responseFlux;
        if (request.getProvider() != null && !request.getProvider().isEmpty()) {
            responseFlux = streamingChatService.chatStream(userInput, history, request.getProvider());
        } else {
            responseFlux = streamingChatService.chatStream(userInput, history);
        }
        
        // 订阅流式响应并发送到WebSocket
        responseFlux.subscribe(
            content -> {
                messagingTemplate.convertAndSend("/topic/chat/" + sessionId, 
                        Map.of("type", "chunk", "content", content));
            },
            error -> {
                messagingTemplate.convertAndSend("/topic/chat/" + sessionId, 
                        Map.of("type", "error", "message", error.getMessage()));
            },
            () -> {
                // 完成时，将完整响应添加到历史
                String fullResponse = responseFlux.collectList().block().stream().reduce("", String::concat);
                history.add(new UserMessage(userInput));
                history.add(new AssistantMessage(fullResponse));
                
                messagingTemplate.convertAndSend("/topic/chat/" + sessionId, 
                        Map.of("type", "complete", "sessionId", sessionId));
            }
        );
    }
    
    /**
     * 清除会话历史
     *
     * @param sessionId 会话ID
     */
    public void clearSessionHistory(String sessionId) {
        sessionStore.remove(sessionId);
    }
} 