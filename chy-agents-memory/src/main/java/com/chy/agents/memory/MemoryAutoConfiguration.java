package com.chy.agents.memory;

import com.chy.agents.memory.longterm.LongTermMemory;
import com.chy.agents.memory.shortterm.ShortTermMemory;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * 记忆模块自动配置
 * 用于自动配置记忆相关的组件
 */
@AutoConfiguration
@ComponentScan(basePackageClasses = {Memory.class})
public class MemoryAutoConfiguration {

    /**
     * 配置短期记忆
     */
    @Bean
    @ConditionalOnMissingBean
    public ShortTermMemory shortTermMemory() {
        return new ShortTermMemory();
    }
    
    /**
     * 配置长期记忆
     * 需要依赖VectorStore和EmbeddingClient
     */
    @Bean
    @ConditionalOnBean({VectorStore.class, EmbeddingClient.class})
    @ConditionalOnMissingBean
    public LongTermMemory longTermMemory(VectorStore vectorStore, EmbeddingClient embeddingClient) {
        return new LongTermMemory(vectorStore, embeddingClient);
    }
} 