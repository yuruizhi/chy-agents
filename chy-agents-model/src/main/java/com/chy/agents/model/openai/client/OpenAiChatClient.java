package com.chy.agents.model.openai.client;

import com.chy.agents.core.chat.ChatClient;
import com.chy.agents.core.chat.message.BaseMessage;
import com.chy.agents.core.chat.message.Message;
import com.chy.agents.core.chat.prompt.Prompt;
import com.chy.agents.model.openai.config.OpenAiConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * OpenAI客户端适配器
 */
@Slf4j
public class OpenAiChatClient implements ChatClient {
    
    private final OpenAiApi openAiApi;
    private final OpenAiConfig config;
    
    public OpenAiChatClient(OpenAiApi openAiApi, OpenAiConfig config) {
        this.openAiApi = openAiApi;
        this.config = config;
    }
    
    @Override
    public Message call(Prompt prompt) {
        try {
            OpenAiChatOptions options = buildChatOptions();
            
            // 使用prompt的toSpringAiMessages方法转换消息格式
            List<org.springframework.ai.chat.messages.Message> messages = prompt.toSpringAiMessages();
            
            // 调用Spring AI的OpenAI API
            ChatResponse response = openAiApi.chatCompletion(messages, options);
            String content = response.getResult().getOutput().getContent();
            
            return BaseMessage.assistantMessage(content);
        } catch (Exception e) {
            log.error("Failed to call OpenAI model", e);
            throw new RuntimeException("Failed to call OpenAI model", e);
        }
    }
    
    @Override
    public CompletableFuture<Message> callAsync(Prompt prompt) {
        return CompletableFuture.supplyAsync(() -> call(prompt));
    }
    
    @Override
    public List<Message> stream(Prompt prompt) {
        try {
            OpenAiChatOptions options = buildChatOptions().withStream(true);
            List<org.springframework.ai.chat.messages.Message> messages = prompt.toSpringAiMessages();
            
            CountDownLatch completionLatch = new CountDownLatch(1);
            List<Message> resultMessages = new CopyOnWriteArrayList<>();
            StringBuilder contentBuilder = new StringBuilder();
            
            // 使用Spring AI的流式API
            openAiApi.streamingChatCompletion(messages, options).subscribe(
                chunk -> {
                    // 处理流式响应块
                    String content = chunk.getResult().getOutput().getContent();
                    if (content != null && !content.isEmpty()) {
                        contentBuilder.append(content);
                        Message message = BaseMessage.assistantMessage(contentBuilder.toString());
                        synchronized (resultMessages) {
                            resultMessages.clear();
                            resultMessages.add(message);
                        }
                    }
                },
                error -> {
                    log.error("Streaming error", error);
                    completionLatch.countDown();
                },
                () -> {
                    log.debug("Streaming completed");
                    completionLatch.countDown();
                }
            );
            
            // 等待流式处理完成或超时
            if (!completionLatch.await(config.getTimeout(), TimeUnit.SECONDS)) {
                log.warn("Streaming timed out after {} seconds", config.getTimeout());
            }
            
            return resultMessages.isEmpty() ? Collections.emptyList() : new ArrayList<>(resultMessages);
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
            "maxTokens", config.getMaxTokens(),
            "endpoint", config.getEndpoint()
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
    
    private OpenAiChatOptions buildChatOptions() {
        return OpenAiChatOptions.builder()
            .withModel(config.getModel())
            .withTemperature(config.getTemperature())
            .withMaxTokens(config.getMaxTokens())
            .build();
    }
} 