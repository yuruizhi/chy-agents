package com.chy.agents.rag.embeddings;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 嵌入服务
 * 负责文本向量化处理
 */
@Service
public class EmbeddingService {

    private final EmbeddingClient embeddingClient;

    /**
     * 构造函数
     * 
     * @param embeddingClient Spring AI的嵌入客户端
     */
    public EmbeddingService(EmbeddingClient embeddingClient) {
        this.embeddingClient = embeddingClient;
    }

    /**
     * 对单个文本进行向量化
     * 
     * @param text 待向量化文本
     * @return 向量表示
     */
    public List<Double> embedText(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("文本不能为空");
        }
        
        try {
            EmbeddingResponse response = embeddingClient.embed(text);
            return response.getResult().getOutput().get(0).getEmbedding();
        } catch (Exception e) {
            throw new RuntimeException("向量化处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量对文本进行向量化
     * 
     * @param texts 待向量化文本列表
     * @return 向量表示列表
     */
    public List<List<Double>> embedTexts(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            EmbeddingResponse response = embeddingClient.embed(texts);
            return response.getResult().getOutput().stream()
                .map(output -> output.getEmbedding())
                .toList();
        } catch (Exception e) {
            throw new RuntimeException("批量向量化处理失败: " + e.getMessage(), e);
        }
    }
} 