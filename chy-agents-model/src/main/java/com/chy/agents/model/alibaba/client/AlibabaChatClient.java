package com.chy.agents.model.alibaba.client;

import com.chy.agents.core.chat.ChatClient;
import com.chy.agents.core.chat.message.BaseMessage;
import com.chy.agents.core.chat.message.Message;
import com.chy.agents.core.chat.prompt.Prompt;
import com.chy.agents.model.alibaba.config.AlibabaConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 阿里云通义客户端适配器
 */
@Slf4j
public class AlibabaChatClient implements ChatClient {
    
    private final RestTemplate restTemplate;
    private final AlibabaConfig config;
    private final Map<String, String> headers;
    
    public AlibabaChatClient(AlibabaConfig config) {
        this.config = config;
        this.restTemplate = new RestTemplate();
        this.headers = new HashMap<>();
        
        // 设置请求头
        headers.put("Authorization", "Bearer " + config.getApiKey());
        headers.put("Content-Type", "application/json");
        if (config.getAccessKeyId() != null) {
            headers.put("x-acm-access-key-id", config.getAccessKeyId());
            headers.put("x-acm-access-key-secret", config.getAccessKeySecret());
        }
    }
    
    @Override
    public Message call(Prompt prompt) {
        try {
            Map<String, Object> requestBody = buildRequestBody(prompt);
            
            // 发送请求
            Map<String, Object> response = restTemplate.postForObject(
                config.getEndpoint(),
                createHttpEntity(requestBody),
                Map.class
            );
            
            // 解析响应
            if (response != null && response.containsKey("output")) {
                Map<String, Object> output = (Map<String, Object>) response.get("output");
                String content = (String) output.get("text");
                return BaseMessage.assistantMessage(content);
            }
            
            throw new RuntimeException("Invalid response from Alibaba model");
        } catch (Exception e) {
            log.error("Failed to call Alibaba model", e);
            throw new RuntimeException("Failed to call Alibaba model", e);
        }
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
            List<Message> messages = new CopyOnWriteArrayList<>(); // 使用线程安全列表
            
            Consumer<Message> messageHandler = messages::add;
            Consumer<Throwable> errorHandler = error -> {
                log.error("Streaming error", error);
                completionLatch.countDown();
            };
            Runnable completionHandler = completionLatch::countDown;
            
            AlibabaStreamHandler streamHandler = new AlibabaStreamHandler(config.getEndpoint(), headers)
                .onMessage(messageHandler)
                .onError(errorHandler)
                .onComplete(completionHandler);
            
            streamHandler.stream(requestBody);
            
            try {
                // 设置适当的超时时间，避免无限等待
                if (!completionLatch.await(config.getTimeout(), TimeUnit.SECONDS)) {
                    log.warn("Streaming timed out after {} seconds", config.getTimeout());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Streaming interrupted", e);
            }
            
            // 返回空列表而不是null，以符合接口规范
            return messages.isEmpty() ? Collections.emptyList() : new ArrayList<>(messages);
        } catch (Exception e) {
            log.error("Failed to stream from Alibaba model", e);
            throw new RuntimeException("Failed to stream from Alibaba model", e);
        }
    }
    
    @Override
    public Map<String, Object> getConfig() {
        return Map.of(
            "provider", getProvider(),
            "model", getModel(),
            "endpoint", config.getEndpoint()
        );
    }
    
    @Override
    public String getProvider() {
        return "alibaba";
    }
    
    @Override
    public String getModel() {
        return config.getModel();
    }
    
    private Map<String, Object> buildRequestBody(Prompt prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", config.getModel());
        
        List<Map<String, String>> messages = new ArrayList<>();
        
        // 添加系统提示
        if (prompt.getSystemPrompt() != null) {
            messages.add(Map.of(
                "role", "system",
                "content", prompt.getSystemPrompt()
            ));
        }
        
        // 添加历史消息
        for (Message message : prompt.getHistory()) {
            messages.add(Map.of(
                "role", convertRole(message.getRole()),
                "content", message.getContent()
            ));
        }
        
        // 添加用户输入
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
    
    private org.springframework.http.HttpEntity<Map<String, Object>> createHttpEntity(Map<String, Object> body) {
        org.springframework.http.HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
        headers.forEach(httpHeaders::add);
        return new org.springframework.http.HttpEntity<>(body, httpHeaders);
    }
} 