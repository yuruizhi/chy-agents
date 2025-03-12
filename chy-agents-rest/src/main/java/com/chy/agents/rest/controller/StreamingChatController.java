package com.chy.agents.rest.controller;

import com.chy.agents.chat.service.StreamingChatService;
import com.chy.agents.chat.service.SessionManager;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

/**
 * 流式聊天控制器
 *
 * @author YuRuizhi
 * @date 2025/3/12
 */
@Controller
public class StreamingChatController {

    private final StreamingChatService streamingChatService;
    private final SessionManager sessionManager;
    private final SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    public StreamingChatController(
            StreamingChatService streamingChatService, 
            SessionManager sessionManager,
            SimpMessagingTemplate messagingTemplate) {
        this.streamingChatService = streamingChatService;
        this.sessionManager = sessionManager;
        this.messagingTemplate = messagingTemplate;
    }
    
    @MessageMapping("/chat.stream")
    public void streamChat(@Payload Map<String, Object> request) {
        String sessionId = (String) request.get("sessionId");
        String userInput = (String) request.get("message");
        
        // 如果没有会话ID，创建新的会话
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = sessionManager.createSession();
        }
        
        // 获取会话历史
        List<Message> chatHistory = sessionManager.getSessionHistory(sessionId);
        
        // 记录用户消息
        UserMessage userMessage = new UserMessage(userInput);
        sessionManager.addMessage(sessionId, userMessage);
        
        // 发送消息给客户端，告知会话已创建
        messagingTemplate.convertAndSend(
            "/topic/chat.session." + sessionId, 
            Map.of("type", "SESSION_CREATED", "sessionId", sessionId)
        );
        
        // 订阅流并将每个响应片段发送给客户端
        String finalSessionId = sessionId;
        streamingChatService.chatStream(userInput, chatHistory)
            .subscribe(
                content -> {
                    messagingTemplate.convertAndSend(
                        "/topic/chat.response." + finalSessionId,
                        Map.of("type", "STREAM_CONTENT", "content", content)
                    );
                },
                error -> {
                    messagingTemplate.convertAndSend(
                        "/topic/chat.response." + finalSessionId,
                        Map.of("type", "ERROR", "message", error.getMessage())
                    );
                },
                () -> {
                    messagingTemplate.convertAndSend(
                        "/topic/chat.response." + finalSessionId,
                        Map.of("type", "COMPLETED")
                    );
                }
            );
    }
} 