package com.chy.agents.rag.embeddings;

import org.springframework.ai.document.Document;
import java.util.List;

/**
 * 文档分块策略接口
 *
 * @author YuRuizhi
 * @date 2025/3/12
 */
public interface ChunkingStrategy {
    /**
     * 将文档分块
     * 
     * @param document 要分块的文档
     * @param maxChunkSize 每块最大大小
     * @param overlap 块间重叠大小
     * @return 分块后的文档列表
     */
    List<Document> chunk(Document document, int maxChunkSize, int overlap);
}