package com.chy.agents.core.agent;

import org.springframework.ai.chat.messages.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单的内存实现
 *
 * @author YuRuizhi
 * @date 2025/3/12
 */
public class SimpleMemory implements Agent.Memory {
    private final List<Message> messages = new ArrayList<>();

    @Override
    public void add(Message message) {
        messages.add(message);
    }

    @Override
    public List<Message> get(int limit) {
        int start = Math.max(0, messages.size() - limit);
        return messages.subList(start, messages.size());
    }

    @Override
    public void clear() {
        messages.clear();
    }
}