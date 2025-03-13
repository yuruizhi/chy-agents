package com.chy.agents.core.agent;

import com.chy.agents.core.chat.ChatClient;
import com.chy.agents.core.chat.message.Message;
import com.chy.agents.core.chat.prompt.Prompt;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 基础代理实现
 */
public class BaseAgent implements Agent {
    
    @Getter
    protected final AgentConfig config;
    
    protected final ChatClient chatClient;
    
    @Getter
    @Setter
    protected String name;
    
    @Getter
    @Setter
    protected String description;
    
    @Getter
    @Setter
    protected List<Tool> tools = new ArrayList<>();
    
    @Getter
    @Setter
    protected Memory memory;
    
    public BaseAgent(AgentConfig config, ChatClient chatClient) {
        this.config = config;
        this.chatClient = chatClient;
        this.name = config.getName();
        this.description = config.getDescription();
    }
    
    @Override
    public AgentResponse execute(String input, Map<String, Object> context) {
        try {
            Message response = chatClient.call(Prompt.of(input));
            return AgentResponse.builder()
                    .content(response.getContent())
                    .type(AgentResponse.ResponseType.TEXT)
                    .status(AgentResponse.ExecutionStatus.SUCCESS)
                    .metadata(context)
                    .build();
        } catch (Exception e) {
            return AgentResponse.builder()
                    .error(e.getMessage())
                    .type(AgentResponse.ResponseType.ERROR)
                    .status(AgentResponse.ExecutionStatus.FAILED)
                    .build();
        }
    }
    
    @Override
    public CompletableFuture<AgentResponse> executeAsync(String input, Map<String, Object> context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return execute(input, context);
            } catch (Exception e) {
                throw new RuntimeException("Async execution failed", e);
            }
        }).orTimeout(config.getTimeout().toSeconds(), TimeUnit.SECONDS);
    }
    
    @Override
    public void reset() {
        if (memory != null) {
            memory.clear();
        }
    }
    
    @Override
    public void close() {
        reset();
    }
} 