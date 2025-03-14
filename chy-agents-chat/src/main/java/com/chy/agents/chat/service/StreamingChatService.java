package com.chy.agents.chat.service;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.ArrayList;

/**
 * 流式聊天服务
 *
 * @author YuRuizhi
 * @date 2025/3/12
 */
@Service
public class StreamingChatService {

    @Resource
    private ChatClient chatClient;
    
    @Autowired(required = false)
    @Qualifier("alibabaChatClient")
    private ChatClient alibabaChatClient;
    
    /**
     * 流式聊天，使用默认聊天客户端
     *
     * @param userInput 用户输入
     * @param chatHistory 聊天历史
     * @return 流式响应
     */
    public Flux<String> chatStream(String userInput, List<Message> chatHistory) {
        List<Message> messages = new ArrayList<>(chatHistory);
        messages.add(new UserMessage(userInput));
        
        return chatClient.prompt()
                .messages(messages)
                .stream()
                .content();
    }
    
    /**
     * 流式聊天，指定提供商
     *
     * @param userInput 用户输入
     * @param chatHistory 聊天历史
     * @param provider 提供商
     * @return 流式响应
     */
    public Flux<String> chatStream(String userInput, List<Message> chatHistory, String provider) {
        ChatClient targetClient = switch (provider.toLowerCase()) {
            case "alibaba" -> alibabaChatClient;
            case "openai" -> chatClient;
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
        
        List<Message> messages = new ArrayList<>(chatHistory);
        messages.add(new UserMessage(userInput));
        
        return targetClient.prompt()
                .messages(messages)
                .stream()
                .content();
    }
} 