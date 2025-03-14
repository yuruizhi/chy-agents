package com.chy.agents.memory.shortterm;

import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 短期记忆实现
 * 用于在对话过程中保存最近的对话消息
 */
@Component
public class ShortTermMemory {

    private final List<Message> messages = new CopyOnWriteArrayList<>();
    private final int maxSize;

    public ShortTermMemory() {
        this(10); // 默认保存最近10条消息
    }

    public ShortTermMemory(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * 添加消息到短期记忆
     *
     * @param message 消息对象
     */
    public void add(Message message) {
        messages.add(message);
        // 如果超过最大大小，删除最旧的消息
        if (messages.size() > maxSize) {
            messages.remove(0);
        }
    }

    /**
     * 获取记忆中的所有消息
     *
     * @return 消息列表
     */
    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    /**
     * 获取指定数量的最近消息
     *
     * @param limit 消息数量
     * @return 消息列表
     */
    public List<Message> getMessages(int limit) {
        int size = messages.size();
        if (size <= limit) {
            return new ArrayList<>(messages);
        }
        return new ArrayList<>(messages.subList(size - limit, size));
    }

    /**
     * 清空短期记忆
     */
    public void clear() {
        messages.clear();
    }
} 