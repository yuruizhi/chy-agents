package com.chy.agents.core.agent;

import lombok.Builder;
import lombok.Data;
import org.springframework.ai.chat.messages.Message;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@Builder
public class Agent {
    private String name;
    private String description;
    private List<Tool> tools;
    private Memory memory;
    
    public String execute(String input) {
        // 实现代理执行逻辑
        return null;
    }
    
    public CompletableFuture<String> executeAsync(String input) {
        // 异步执行
        return CompletableFuture.supplyAsync(() -> execute(input));
    }
    
    public interface Tool {
        String getName();
        String getDescription();
        String execute(String input);
    }
    
    public interface Memory {
        void add(Message message);
        List<Message> get(int limit);
        void clear();
    }
} 