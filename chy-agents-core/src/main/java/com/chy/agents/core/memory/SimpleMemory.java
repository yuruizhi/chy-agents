package com.chy.agents.core.memory;

import com.chy.agents.core.agent.Agent;
import com.chy.agents.core.chat.message.Message;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 简单内存实现
 */
public class SimpleMemory implements Agent.Memory {
    
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final List<Message> messages = new ArrayList<>();
    
    @Getter
    private final int maxSize;
    
    public SimpleMemory() {
        this(100);
    }
    
    public SimpleMemory(int maxSize) {
        this.maxSize = maxSize;
    }
    
    @Override
    public void add(Message message) {
        lock.writeLock().lock();
        try {
            if (messages.size() >= maxSize) {
                messages.remove(0);
            }
            messages.add(message);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public List<Message> get(int limit) {
        lock.readLock().lock();
        try {
            int size = Math.min(limit, messages.size());
            return new ArrayList<>(messages.subList(messages.size() - size, messages.size()));
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            messages.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public List<Message> getAll() {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableList(new ArrayList<>(messages));
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return messages.size();
        } finally {
            lock.readLock().unlock();
        }
    }
} 