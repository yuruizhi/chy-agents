package com.chy.agents.chat.service;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
public class ChatService {

    private final ChatClient chatClient;
    
    @Autowired
    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    
    public String chat(String userInput, List<Message> chatHistory) {
        List<Message> messages = new ArrayList<>(chatHistory);
        messages.add(new UserMessage(userInput));
        
        Prompt prompt = new Prompt(messages);
        ChatResponse response = chatClient.call(prompt);
        
        return response.getResult().getOutput().getContent();
    }
} 