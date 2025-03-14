package com.chy.agents.memory;

import org.springframework.ai.chat.messages.Message;

import java.util.List;
import java.util.Map;

/**
 * 记忆接口
 * 定义记忆系统的基本操作
 */
public interface Memory {

    /**
     * 添加消息到记忆
     *
     * @param message 消息对象
     */
    void add(Message message);

    /**
     * 添加消息及其元数据到记忆
     *
     * @param message 消息对象
     * @param metadata 相关元数据
     */
    void add(Message message, Map<String, Object> metadata);

    /**
     * 获取记忆中的消息
     *
     * @param limit 获取条数限制
     * @return 消息列表
     */
    List<Message> get(int limit);

    /**
     * 根据查询获取相关记忆
     *
     * @param query 查询内容
     * @param limit 结果数量限制
     * @return 相关消息列表
     */
    List<Message> search(String query, int limit);

    /**
     * 清空记忆
     */
    void clear();
} 