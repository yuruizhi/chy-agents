package com.chy.agents.core.chat.adapter;

import com.chy.agents.core.chat.ChatClient;
import com.chy.agents.core.chat.message.BaseMessage;
import com.chy.agents.core.chat.message.Message;
import com.chy.agents.core.chat.prompt.Prompt;
import org.springframework.ai.chat.client.ChatResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Spring AI ChatClient适配器
 * 将Spring AI的ChatClient适配为系统内部的ChatClient接口
 *
 * @author YuRuizhi
 * @date 2025/3/14
 */
public class SpringAiChatClientAdapter implements ChatClient {

    private final org.springframework.ai.chat.client.ChatClient springAiChatClient;
    private final String provider;
    private final String model;
    private final Map<String, Object> config;

    public SpringAiChatClientAdapter(org.springframework.ai.chat.client.ChatClient springAiChatClient, 
                                    String provider, 
                                    String model, 
                                    Map<String, Object> config) {
        this.springAiChatClient = springAiChatClient;
        this.provider = provider;
        this.model = model;
        this.config = config != null ? config : new HashMap<>();
    }

    @Override
    public Message call(Prompt prompt) {
        List<org.springframework.ai.chat.messages.Message> springMessages = prompt.toSpringAiMessages();
        
        org.springframework.ai.chat.ChatResponse response = springAiChatClient.prompt()
                .messages(springMessages)
                .call();
        
        return BaseMessage.assistantMessage(response.getResult().getOutput().getContent());
    }

    @Override
    public CompletableFuture<Message> callAsync(Prompt prompt) {
        List<org.springframework.ai.chat.messages.Message> springMessages = prompt.toSpringAiMessages();
        
        return CompletableFuture.supplyAsync((Supplier<Message>) () -> {
            org.springframework.ai.chat.ChatResponse response = springAiChatClient.prompt()
                    .messages(springMessages)
                    .call();
            return BaseMessage.assistantMessage(response.getResult().getOutput().getContent());
        });
    }

    @Override
    public List<Message> stream(Prompt prompt) {
        List<org.springframework.ai.chat.messages.Message> springMessages = prompt.toSpringAiMessages();
        
        List<String> chunks = springAiChatClient.prompt()
                .messages(springMessages)
                .stream()
                .content()
                .collectList()
                .block();
        
        List<Message> messages = new ArrayList<>();
        if (chunks != null) {
            String fullContent = String.join("", chunks);
            messages.add(BaseMessage.assistantMessage(fullContent));
        }
        
        return messages;
    }

    @Override
    public Map<String, Object> getConfig() {
        return this.config;
    }

    @Override
    public String getProvider() {
        return this.provider;
    }

    @Override
    public String getModel() {
        return this.model;
    }
} 