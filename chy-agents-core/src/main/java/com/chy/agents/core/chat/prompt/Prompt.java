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
} 