package com.chy.agents.core.agent;

import java.util.HashMap;
import java.util.Map;

/**
 * 代理响应类
 * 表示智能代理执行任务后的响应结果
 */
public class AgentResponse {
    
    /**
     * 响应类型枚举
     */
    public enum ResponseType {
        /**
         * 文本响应
         */
        TEXT,
        
        /**
         * JSON响应
         */
        JSON,
        
        /**
         * 二进制响应
         */
        BINARY,
        
        /**
         * 错误响应
         */
        ERROR
    }
    
    /**
     * 执行状态枚举
     */
    public enum ExecutionStatus {
        /**
         * 执行成功
         */
        SUCCESS,
        
        /**
         * 执行失败
         */
        FAILED,
        
        /**
         * 执行部分成功
         */
        PARTIAL_SUCCESS,
        
        /**
         * 执行超时
         */
        TIMEOUT
    }
    
    // 响应内容
    private String content;
    
    // 响应类型
    private ResponseType type;
    
    // 执行状态
    private ExecutionStatus status;
    
    // 错误信息
    private String error;
    
    // 元数据
    private Map<String, Object> metadata;
    
    /**
     * 私有构造函数，通过Builder模式创建
     */
    private AgentResponse() {
        this.metadata = new HashMap<>();
    }
    
    /**
     * 获取响应内容
     * 
     * @return 响应内容
     */
    public String getContent() {
        return content;
    }
    
    /**
     * 获取响应类型
     * 
     * @return 响应类型
     */
    public ResponseType getType() {
        return type;
    }
    
    /**
     * 获取执行状态
     * 
     * @return 执行状态
     */
    public ExecutionStatus getStatus() {
        return status;
    }
    
    /**
     * 获取错误信息
     * 
     * @return 错误信息
     */
    public String getError() {
        return error;
    }
    
    /**
     * 获取元数据
     * 
     * @return 元数据
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    /**
     * 获取指定键的元数据值
     * 
     * @param key 元数据键
     * @return 元数据值
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }
    
    /**
     * 检查响应是否成功
     * 
     * @return 是否成功
     */
    public boolean isSuccess() {
        return status == ExecutionStatus.SUCCESS;
    }
    
    /**
     * 检查响应是否失败
     * 
     * @return 是否失败
     */
    public boolean isError() {
        return status == ExecutionStatus.FAILED;
    }
    
    /**
     * 创建一个成功的文本响应
     * 
     * @param content 响应内容
     * @return 响应对象
     */
    public static AgentResponse ofText(String content) {
        return builder()
                .content(content)
                .type(ResponseType.TEXT)
                .status(ExecutionStatus.SUCCESS)
                .build();
    }
    
    /**
     * 创建一个错误响应
     * 
     * @param error 错误信息
     * @return 响应对象
     */
    public static AgentResponse ofError(String error) {
        return builder()
                .error(error)
                .type(ResponseType.ERROR)
                .status(ExecutionStatus.FAILED)
                .build();
    }
    
    /**
     * 获取Builder对象
     * 
     * @return Builder对象
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * AgentResponse的构建器类
     */
    public static class Builder {
        private final AgentResponse response;
        
        /**
         * 构造函数
         */
        private Builder() {
            response = new AgentResponse();
        }
        
        /**
         * 设置响应内容
         * 
         * @param content 响应内容
         * @return Builder对象
         */
        public Builder content(String content) {
            response.content = content;
            return this;
        }
        
        /**
         * 设置响应类型
         * 
         * @param type 响应类型
         * @return Builder对象
         */
        public Builder type(ResponseType type) {
            response.type = type;
            return this;
        }
        
        /**
         * 设置执行状态
         * 
         * @param status 执行状态
         * @return Builder对象
         */
        public Builder status(ExecutionStatus status) {
            response.status = status;
            return this;
        }
        
        /**
         * 设置错误信息
         * 
         * @param error 错误信息
         * @return Builder对象
         */
        public Builder error(String error) {
            response.error = error;
            return this;
        }
        
        /**
         * 设置元数据
         * 
         * @param metadata 元数据
         * @return Builder对象
         */
        public Builder metadata(Map<String, Object> metadata) {
            if (metadata != null) {
                response.metadata.putAll(metadata);
            }
            return this;
        }
        
        /**
         * 添加元数据
         * 
         * @param key 元数据键
         * @param value 元数据值
         * @return Builder对象
         */
        public Builder addMetadata(String key, Object value) {
            response.metadata.put(key, value);
            return this;
        }
        
        /**
         * 构建AgentResponse对象
         * 
         * @return AgentResponse对象
         */
        public AgentResponse build() {
            return response;
        }
    }
} 