package com.chy.agents.core.chat.prompt;

import com.chy.agents.core.chat.message.Message;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 提示信息
 */
@Data
@Builder
public class Prompt {
    
    /**
     * 系统提示
     */
    private String systemPrompt;
    
    /**
     * 用户输入
     */
    private String userInput;
    
    /**
     * 历史消息
     */
    @Builder.Default
    private List<Message> history = new ArrayList<>();
    
    /**
     * 配置参数
     */
    @Builder.Default
    private Map<String, Object> parameters = Map.of();
    
    /**
     * 创建简单提示
     *
     * @param input 用户输入
     * @return 提示实例
     */
    public static Prompt of(String input) {
        return Prompt.builder()
                .userInput(input)
                .build();
    }
    
    /**
     * 创建带系统提示的实例
     *
     * @param system 系统提示
     * @param input 用户输入
     * @return 提示实例
     */
    public static Prompt of(String system, String input) {
        return Prompt.builder()
                .systemPrompt(system)
                .userInput(input)
                .build();
    }
    
    /**
     * 转换为Spring AI兼容的消息列表
     * 这个方法在项目集成Spring AI时使用
     * 
     * @return Spring AI消息列表
     */
    public List<org.springframework.ai.chat.messages.Message> toSpringAiMessages() {
        List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();
        
        // 添加系统提示
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            messages.add(new org.springframework.ai.chat.messages.SystemMessage(systemPrompt));
        }
        
        // 添加历史消息
        for (Message message : history) {
            messages.add(convertToSpringAiMessage(message));
        }
        
        // 添加用户输入
        messages.add(new org.springframework.ai.chat.messages.UserMessage(userInput));
        
        return messages;
    }
    
    /**
     * 将内部消息转换为Spring AI消息
     * 
     * @param message 内部消息
     * @return Spring AI消息
     */
    private org.springframework.ai.chat.messages.Message convertToSpringAiMessage(Message message) {
        return switch (message.getRole()) {
            case SYSTEM -> new org.springframework.ai.chat.messages.SystemMessage(message.getContent());
            case USER -> new org.springframework.ai.chat.messages.UserMessage(message.getContent());
            case ASSISTANT -> new org.springframework.ai.chat.messages.AssistantMessage(message.getContent());
            case FUNCTION -> new org.springframework.ai.chat.messages.UserMessage(message.getContent()); // Spring AI暂无Function类型
        };
    }
} 