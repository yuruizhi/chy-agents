package com.chy.agents.alibaba.client;


import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;

public class AlibabaChatClientAdapter implements ChatClient {
    
    private final Generation generation;
    
    public AlibabaChatClientAdapter(Generation generation) {
        this.generation = generation;
    }

    @Override
    public String call(Prompt prompt) {
        GenerationResult result = generation.call(convertPrompt(prompt));
        return result.getOutput().getText();
    }
    
    private Prompt convertPrompt(Prompt prompt) {
        // 实现Prompt转换逻辑
    }
} 