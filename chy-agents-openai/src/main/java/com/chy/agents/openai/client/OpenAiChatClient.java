package com.chy.agents.openai.client;

import com.chy.agents.core.chat.ChatClient;
import com.chy.agents.core.chat.message.BaseMessage;
import com.chy.agents.core.chat.message.Message;
import com.chy.agents.core.chat.prompt.Prompt;
import com.chy.agents.openai.config.OpenAiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * OpenAI客户端适配器
 */
@Slf4j
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
        
        // Add history messages
        for (Message message : prompt.getHistory()) {
            messages.add(convertMessage(message));
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
        try {
            Map<String, Object> requestBody = buildRequestBody(prompt);
            requestBody.put("stream", true);
            
            CountDownLatch completionLatch = new CountDownLatch(1);
            List<Message> messages = new ArrayList<>();
            Consumer<Message> messageHandler = messages::add;
            Consumer<Throwable> errorHandler = error -> {
                log.error("Streaming error", error);
                completionLatch.countDown();
            };
            Runnable completionHandler = completionLatch::countDown;
            
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + config.getApiKey());
            headers.put("Content-Type", "application/json");
            if (config.getOrganizationId() != null) {
                headers.put("OpenAI-Organization", config.getOrganizationId());
            }
            
            String endpoint = config.getEndpoint() + "/v1/chat/completions";
            OpenAiStreamHandler streamHandler = new OpenAiStreamHandler(endpoint, headers)
                .onMessage(messageHandler)
                .onError(errorHandler)
                .onComplete(completionHandler);
            
            List<Message> streamMessages = streamHandler.stream(requestBody);
            
            try {
                // Wait for streaming to complete or timeout
                if (!completionLatch.await(config.getTimeout(), TimeUnit.SECONDS)) {
                    log.warn("Streaming timed out after {} seconds", config.getTimeout());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Streaming interrupted", e);
            }
            
            return streamMessages.isEmpty() ? Collections.emptyList() : streamMessages;
        } catch (Exception e) {
            log.error("Failed to stream from OpenAI model", e);
            throw new RuntimeException("Failed to stream from OpenAI model", e);
        }
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
    
    private Map<String, Object> buildRequestBody(Prompt prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", config.getModel());
        
        List<Map<String, String>> messages = new ArrayList<>();
        
        // Add system prompt
        if (prompt.getSystemPrompt() != null) {
            messages.add(Map.of(
                "role", "system",
                "content", prompt.getSystemPrompt()
            ));
        }
        
        // Add history messages
        for (Message message : prompt.getHistory()) {
            messages.add(Map.of(
                "role", convertRole(message.getRole()),
                "content", message.getContent()
            ));
        }
        
        // Add user input
        messages.add(Map.of(
            "role", "user",
            "content", prompt.getUserInput()
        ));
        
        requestBody.put("messages", messages);
        requestBody.put("temperature", config.getTemperature());
        requestBody.put("max_tokens", config.getMaxTokens());
        
        return requestBody;
    }
    
    private String convertRole(Message.Role role) {
        return switch (role) {
            case SYSTEM -> "system";
            case USER -> "user";
            case ASSISTANT -> "assistant";
            case FUNCTION -> "function";
        };
    }
    
    private org.springframework.ai.chat.messages.Message convertMessage(Message message) {
        return switch (message.getRole()) {
            case SYSTEM -> new org.springframework.ai.chat.messages.SystemMessage(message.getContent());
            case USER -> new org.springframework.ai.chat.messages.UserMessage(message.getContent());
            case ASSISTANT -> new org.springframework.ai.chat.messages.AssistantMessage(message.getContent());
            case FUNCTION -> new org.springframework.ai.chat.messages.FunctionMessage(message.getContent(), "");
        };
    }
} 