package com.chy.agents.rag.service;

import com.chy.agents.rag.chunk.TextChunker;
import com.chy.agents.rag.chunk.TextChunker.DocumentChunk;
import com.chy.agents.rag.embeddings.EmbeddingService;
import com.chy.agents.rag.vector.VectorStoreService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

/**
 * RAG服务
 * 提供检索增强生成的核心功能
 */
@Service
public class RagService {

    private final TextChunker textChunker;
    private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;

    /**
     * 构造函数
     * 
     * @param textChunker 文本分块器
     * @param embeddingService 嵌入服务
     * @param vectorStoreService 向量存储服务
     */
    public RagService(
            TextChunker textChunker,
            EmbeddingService embeddingService,
            VectorStoreService vectorStoreService) {
        this.textChunker = textChunker;
        this.embeddingService = embeddingService;
        this.vectorStoreService = vectorStoreService;
    }

    /**
     * 添加文档到知识库
     * 
     * @param content 文档内容
     * @param title 文档标题
     * @return 文档ID
     */
    public String addDocument(String content, String title) {
        String documentId = UUID.randomUUID().toString();
        
        // 1. 分块
        List<DocumentChunk> chunks = textChunker.chunkWithMetadata(content, documentId, title);
        
        // 2. 存储每个分块
        for (int i = 0; i < chunks.size(); i++) {
            DocumentChunk chunk = chunks.get(i);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("documentId", chunk.getDocumentId());
            metadata.put("documentTitle", chunk.getDocumentTitle());
            metadata.put("chunkIndex", chunk.getChunkIndex());
            
            String chunkId = documentId + "-" + i;
            vectorStoreService.addDocument(chunk.getContent(), chunkId, metadata);
        }
        
        return documentId;
    }
    
    /**
     * 根据查询检索相关文档
     * 
     * @param query 查询文本
     * @param limit 返回结果数量限制
     * @return 相关文档列表
     */
    public List<Document> query(String query, int limit) {
        return vectorStoreService.searchSimilar(query, limit);
    }
    
    /**
     * 删除文档
     * 
     * @param documentId 文档ID
     */
    public void deleteDocument(String documentId) {
        // 实际应用中，应该先查询出所有与该文档相关的分块，然后批量删除
        // 这里简化实现
        vectorStoreService.deleteDocument(documentId);
    }
}
