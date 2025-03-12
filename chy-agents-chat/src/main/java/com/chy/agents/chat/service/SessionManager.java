package com.chy.agents.chat.service;

import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionManager {
    
    private final Map<String, List<Message>> sessions = new ConcurrentHashMap<>();
    
    /**
     * 创建新的会话
     * @return 会话ID
     */
    public String createSession() {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, new ArrayList<>());
        return sessionId;
    }
    
    /**
     * 获取会话历史
     * @param sessionId 会话ID
     * @return 消息列表
     */
    public List<Message> getSessionHistory(String sessionId) {
        return sessions.getOrDefault(sessionId, new ArrayList<>());
    }
    
    /**
     * 添加消息到会话
     * @param sessionId 会话ID
     * @param message 消息
     */
    public void addMessage(String sessionId, Message message) {
        sessions.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(message);
    }
    
    /**
     * 清除会话历史
     * @param sessionId 会话ID
     */
    public void clearSession(String sessionId) {
        sessions.put(sessionId, new ArrayList<>());
    }
    
    /**
     * 删除会话
     * @param sessionId 会话ID
     */
    public void deleteSession(String sessionId) {
        sessions.remove(sessionId);
    }
    
    /**
     * 获取所有会话ID
     * @return 会话ID列表
     */
    public List<String> getAllSessions() {
        return new ArrayList<>(sessions.keySet());
    }
} 