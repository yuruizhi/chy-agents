package com.chy.agents.memory.longterm;

import com.chy.agents.memory.Memory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 长期记忆实现
 * 用于存储和检索历史对话中的重要信息
 */
@Component
public class LongTermMemory implements Memory {

    private final VectorStore vectorStore;
    private final EmbeddingClient embeddingClient;

    @Autowired(required = false)
    public LongTermMemory(VectorStore vectorStore, EmbeddingClient embeddingClient) {
        this.vectorStore = vectorStore;
        this.embeddingClient = embeddingClient;
    }

    /**
     * 添加消息到长期记忆
     *
     * @param message 消息对象
     */
    @Override
    public void add(Message message) {
        add(message, new HashMap<>());
    }

    /**
     * 添加消息及其元数据到记忆
     *
     * @param message 消息对象
     * @param metadata 相关元数据
     */
    @Override
    public void add(Message message, Map<String, Object> metadata) {
        if (vectorStore == null || embeddingClient == null) {
            return;
        }
        
        String id = UUID.randomUUID().toString();
        String content = message.getContent();
        
        // 添加消息类型到元数据
        Map<String, Object> enhancedMetadata = new HashMap<>(metadata);
        enhancedMetadata.put("messageType", message.getMessageType().toString());
        enhancedMetadata.put("timestamp", System.currentTimeMillis());
        
        try {
            vectorStore.add(List.of(content), List.of(id), List.of(enhancedMetadata));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取记忆中的消息
     * 长期记忆不支持直接获取，返回空列表
     *
     * @param limit 获取条数限制
     * @return 消息列表
     */
    @Override
    public List<Message> get(int limit) {
        // 长期记忆不支持直接获取，需要通过搜索操作
        return new ArrayList<>();
    }

    /**
     * 根据查询获取相关记忆
     *
     * @param query 查询内容
     * @param limit 结果数量限制
     * @return 相关消息列表
     */
    @Override
    public List<Message> search(String query, int limit) {
        if (vectorStore == null || embeddingClient == null) {
            return new ArrayList<>();
        }
        
        try {
            SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(limit)
                .build();
                
            return vectorStore.similaritySearch(request).stream()
                .map(document -> new UserMessage(
                    document.getContent(), 
                    document.getMetadata().getOrDefault("messageType", "user").toString()
                ))
                .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 清空记忆
     * 长期记忆不支持清空操作
     */
    @Override
    public void clear() {
        // 长期记忆不支持完全清空，需要通过特定操作删除
    }
    
    /**
     * 根据ID删除记忆
     *
     * @param id 记忆ID
     */
    public void delete(String id) {
        if (vectorStore == null) {
            return;
        }
        
        try {
            vectorStore.delete(List.of(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 