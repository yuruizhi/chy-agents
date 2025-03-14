package com.chy.agents.model.private.client;

import com.chy.agents.core.chat.ChatClient;
import com.chy.agents.model.private.config.PrivateModelConfig;

/**
 * 私有模型客户端接口
 */
public interface PrivateModelClient extends ChatClient {
    
    /**
     * 获取模型类型
     *
     * @return 模型类型
     */
    PrivateModelConfig.ModelType getModelType();
    
    /**
     * 设置模型路径
     *
     * @param modelPath 模型路径
     */
    void setModelPath(String modelPath);
    
    /**
     * 获取模型路径
     *
     * @return 模型路径
     */
    String getModelPath();
    
    /**
     * 是否支持流式处理
     *
     * @return 是否支持
     */
    boolean supportsStreaming();
} 