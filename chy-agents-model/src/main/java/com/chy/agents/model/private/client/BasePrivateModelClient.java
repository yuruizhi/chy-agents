package com.chy.agents.model.private.client;

import com.chy.agents.core.chat.message.BaseMessage;
import com.chy.agents.core.chat.message.Message;
import com.chy.agents.core.chat.prompt.Prompt;
import com.chy.agents.model.private.config.PrivateModelConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 私有模型客户端基础实现
 */
@Slf4j
public abstract class BasePrivateModelClient implements PrivateModelClient {
    
    protected final PrivateModelConfig config;
    
    @Getter
    @Setter
    protected String modelPath;
    
    public BasePrivateModelClient(PrivateModelConfig config) {
        this.config = config;
    }
    
    @Override
    public CompletableFuture<Message> callAsync(Prompt prompt) {
        return CompletableFuture.supplyAsync(() -> call(prompt));
    }
    
    @Override
    public List<Message> stream(Prompt prompt) {
        if (!supportsStreaming()) {
            log.warn("Streaming not supported by this model client. Falling back to sync call.");
            return Collections.singletonList(call(prompt));
        }
        return doStream(prompt);
    }
    
    @Override
    public Map<String, Object> getConfig() {
        return Map.of(
            "provider", getProvider(),
            "model", getModel(),
            "modelType", getModelType().toString(),
            "modelPath", getModelPath() != null ? getModelPath() : "",
            "endpoint", config.getEndpoint(),
            "temperature", config.getTemperature(),
            "maxTokens", config.getMaxTokens()
        );
    }
    
    @Override
    public String getModel() {
        return config.getModel();
    }
    
    @Override
    public PrivateModelConfig.ModelType getModelType() {
        return config.getModelType();
    }
    
    @Override
    public String getProvider() {
        return "private:" + getModelType().toString().toLowerCase();
    }
    
    /**
     * 执行流式处理，由子类实现
     *
     * @param prompt 提示
     * @return 消息列表
     */
    protected abstract List<Message> doStream(Prompt prompt);
} 