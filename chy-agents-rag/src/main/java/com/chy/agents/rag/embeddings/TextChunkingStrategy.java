package com.chy.agents.rag.embeddings;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * 文本分块策略实现
 *
 * @author YuRuizhi
 * @date 2025/3/12
 */
@Component
public class TextChunkingStrategy implements ChunkingStrategy {
    @Override
    public List<Document> chunk(Document document, int maxChunkSize, int overlap) {
        String content = document.getContent();
        Map<String, Object> metadata = document.getMetadata();
        
        List<Document> chunks = new ArrayList<>();
        
        // 简单的按句子分割实现
        String[] sentences = content.split("[.!?]");
        StringBuilder currentChunk = new StringBuilder();
        Map<String, Object> chunkMetadata = Map.copyOf(metadata);
        
        for (String sentence : sentences) {
            // 清理句子
            String trimmedSentence = sentence.trim();
            if (trimmedSentence.isEmpty()) {
                continue;
            }
            
            // 如果当前块加上新句子会超过限制，则创建新块
            if (currentChunk.length() + trimmedSentence.length() > maxChunkSize) {
                if (currentChunk.length() > 0) {
                    chunks.add(new Document(currentChunk.toString(), chunkMetadata));
                    
                    // 保留重叠部分
                    if (overlap > 0 && currentChunk.length() > overlap) {
                        currentChunk = new StringBuilder(
                            currentChunk.substring(currentChunk.length() - overlap)
                        );
                    } else {
                        currentChunk = new StringBuilder();
                    }
                }
            }
            
            // 添加句子到当前块
            if (currentChunk.length() > 0) {
                currentChunk.append(" ");
            }
            currentChunk.append(trimmedSentence).append(".");
        }
        
        // 添加最后剩余的内容
        if (currentChunk.length() > 0) {
            chunks.add(new Document(currentChunk.toString(), chunkMetadata));
        }
        
        return chunks;
    }
} 