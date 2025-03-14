package com.chy.agents.openai.client;

import com.chy.agents.core.chat.ChatClient;
import com.chy.agents.core.chat.message.BaseMessage;
import com.chy.agents.core.chat.message.Message;
import com.chy.agents.core.chat.prompt.Prompt;
import com.chy.agents.openai.config.OpenAiConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * OpenAI客户端适配器
 */
@RequiredArgsConstructor
public class OpenAiChatClient implements ChatClient {
    
    private final OpenAiApi openAiApi;
    private final OpenAiConfig config;
    
    @Override
    public Message call(Prompt prompt) {
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel(config.getModel())
                .withTemperature(config.getTemperature())
                .withMaxTokens(config.getMaxTokens())
                .build();
        
        List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();
        if (prompt.getSystemPrompt() != null) {
            messages.add(new org.springframework.ai.chat.messages.SystemMessage(prompt.getSystemPrompt()));
        }
        messages.add(new org.springframework.ai.chat.messages.UserMessage(prompt.getUserInput()));
        
        ChatResponse response = openAiApi.chatCompletion(messages, options);
        String content = response.getResult().getOutput().getContent();
        
        return BaseMessage.assistantMessage(content);
    }
    
    @Override
    public CompletableFuture<Message> callAsync(Prompt prompt) {
        return CompletableFuture.supplyAsync(() -> call(prompt));
    }
    
    @Override
    public List<Message> stream(Prompt prompt) {
        throw new UnsupportedOperationException("Streaming not implemented yet");
    }
    
    @Override
    public Map<String, Object> getConfig() {
        return Map.of(
            "provider", getProvider(),
            "model", getModel(),
            "temperature", config.getTemperature(),
            "maxTokens", config.getMaxTokens()
        );
    }
    
    @Override
    public String getProvider() {
        return "openai";
    }
    
    @Override
    public String getModel() {
        return config.getModel();
    }
} 