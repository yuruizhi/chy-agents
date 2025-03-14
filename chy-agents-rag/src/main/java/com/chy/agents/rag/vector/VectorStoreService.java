package com.chy.agents.rag.vector;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 向量存储服务
 * 负责管理文档的向量存储与检索
 */
@Service
public class VectorStoreService {

    private final VectorStore vectorStore;

    /**
     * 构造函数
     * 
     * @param vectorStore Spring AI的向量存储
     */
    public VectorStoreService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * 添加文档到向量存储
     * 
     * @param content 文档内容
     * @param metadata 元数据
     * @return 文档ID
     */
    public String addDocument(String content, Map<String, Object> metadata) {
        String id = UUID.randomUUID().toString();
        addDocument(content, id, metadata);
        return id;
    }
    
    /**
     * 添加文档到向量存储（指定ID）
     * 
     * @param content 文档内容
     * @param id 文档ID
     * @param metadata 元数据
     */
    public void addDocument(String content, String id, Map<String, Object> metadata) {
        Map<String, Object> metadataCopy = new HashMap<>(metadata);
        vectorStore.add(List.of(content), List.of(id), List.of(metadataCopy));
    }

    /**
     * 批量添加文档到向量存储
     * 
     * @param contents 文档内容列表
     * @param ids 文档ID列表
     * @param metadata 元数据列表
     */
    public void addDocuments(List<String> contents, List<String> ids, List<Map<String, Object>> metadata) {
        vectorStore.add(contents, ids, metadata);
    }
    
    /**
     * 检索相似文档
     * 
     * @param query 查询文本
     * @param limit 返回结果数量限制
     * @return 相似文档列表
     */
    public List<Document> searchSimilar(String query, int limit) {
        SearchRequest request = SearchRequest.builder()
            .query(query)
            .topK(limit)
            .build();
            
        return vectorStore.similaritySearch(request);
    }
    
    /**
     * 删除文档
     * 
     * @param id 文档ID
     */
    public void deleteDocument(String id) {
        vectorStore.delete(List.of(id));
    }
    
    /**
     * 批量删除文档
     * 
     * @param ids 文档ID列表
     */
    public void deleteDocuments(List<String> ids) {
        vectorStore.delete(ids);
    }
} 