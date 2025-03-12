package com.chy.agents.chat.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.StreamingChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final StreamingChatClient streamingChatClient;
    
    @Autowired
    public StreamingChatService(StreamingChatClient streamingChatClient) {
        this.streamingChatClient = streamingChatClient;
    }
    
    public Flux<String> chatStream(String userInput, List<Message> chatHistory) {
        List<Message> messages = new ArrayList<>(chatHistory);
        messages.add(new UserMessage(userInput));
        
        Prompt prompt = new Prompt(messages);
        return streamingChatClient.call(prompt)
            .map(response -> response.getResult().getOutput().getContent());
    }
} 