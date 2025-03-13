package com.chy.agents.chat.service;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.StreamingChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
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
    private final StreamingChatClient streamingChatClient;
    
    @Autowired(required = false)
    @Qualifier("alibabaChatClient")
    private ChatClient alibabaChatClient;
    
    @Autowired
    public StreamingChatService(StreamingChatClient streamingChatClient) {
        this.streamingChatClient = streamingChatClient;
    }
    
    public Flux<String> chatStream(String userInput, List<Message> chatHistory, String provider) {
        ChatClient targetClient = switch (provider.toLowerCase()) {
            case "alibaba" -> alibabaChatClient;
            case "openai" -> streamingChatClient;
            default -> throw new IllegalArgumentException("Unsupported provider");
        };
        
        List<Message> messages = new ArrayList<>(chatHistory);
        messages.add(new UserMessage(userInput));
        
        Prompt prompt = new Prompt(messages);
        return targetClient.call(prompt)
            .map(response -> response.getResult().getOutput().getContent());
    }
} 