package com.chy.agents.rag;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.chy.agents.rag.chunk.TextChunker;
import com.chy.agents.rag.chunk.SimpleTextChunker;
import com.chy.agents.rag.service.DocumentService;
import com.chy.agents.rag.service.RagService;
import com.chy.agents.rag.embeddings.EmbeddingService;
import com.chy.agents.rag.vector.VectorStoreService;

/**
 * RAG模块自动配置
 * 用于自动配置RAG相关的组件
 */
@AutoConfiguration
@ComponentScan(basePackages = "com.chy.agents.rag")
public class RagAutoConfiguration {

    /**
     * 配置文本分块器
     */
    @Bean
    @ConditionalOnMissingBean
    public TextChunker textChunker() {
        // 默认使用简单分块器，可以通过配置切换为其他实现
        return new SimpleTextChunker(500, 100);
    }
    
    /**
     * 配置嵌入服务
     * 需要依赖EmbeddingClient
     */
    @Bean
    @ConditionalOnBean(EmbeddingClient.class)
    @ConditionalOnMissingBean
    public EmbeddingService embeddingService(EmbeddingClient embeddingClient) {
        return new EmbeddingService(embeddingClient);
    }
    
    /**
     * 配置向量存储服务
     * 需要依赖VectorStore
     */
    @Bean
    @ConditionalOnBean(VectorStore.class)
    @ConditionalOnMissingBean
    public VectorStoreService vectorStoreService(VectorStore vectorStore) {
        return new VectorStoreService(vectorStore);
    }
    
    /**
     * 配置RAG服务
     * 需要依赖各个组件
     */
    @Bean
    @ConditionalOnBean({TextChunker.class, EmbeddingService.class, VectorStoreService.class})
    @ConditionalOnMissingBean
    public RagService ragService(
            TextChunker textChunker,
            EmbeddingService embeddingService,
            VectorStoreService vectorStoreService) {
        return new RagService(textChunker, embeddingService, vectorStoreService);
    }
} 