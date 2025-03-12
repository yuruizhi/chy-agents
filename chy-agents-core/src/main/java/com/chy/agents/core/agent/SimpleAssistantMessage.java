package com.chy.agents.core.agent;

import org.springframework.ai.chat.messages.Message;

/**
 * 简单的助手消息实现
 *
 * @author YuRuizhi
 * @date 2025/3/12
 */
public class SimpleAssistantMessage implements Message {
    private final String content;

    public SimpleAssistantMessage(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getRole() {
        return "assistant";
    }
}