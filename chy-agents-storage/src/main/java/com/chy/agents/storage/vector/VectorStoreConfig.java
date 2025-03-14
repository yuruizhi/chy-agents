package com.chy.agents.storage.vector;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 向量存储配置类
 */
@Configuration
public class VectorStoreConfig {

    /**
     * PostgreSQL 向量存储配置
     */
    @Configuration
    @ConditionalOnClass(PgVectorStore.class)
    @ConditionalOnProperty(prefix = "chy.agents.storage.vector", name = "type", havingValue = "pgvector", matchIfMissing = false)
    static class PgVectorStoreConfig {

        @Bean
        @ConditionalOnBean({JdbcTemplate.class, EmbeddingClient.class})
        public VectorStore pgVectorStore(JdbcTemplate jdbcTemplate, EmbeddingClient embeddingClient) {
            return new PgVectorStore(jdbcTemplate, embeddingClient);
        }
    }
} 