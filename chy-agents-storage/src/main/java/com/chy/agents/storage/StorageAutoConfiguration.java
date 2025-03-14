package com.chy.agents.storage;

import com.chy.agents.storage.cache.CacheStorage;
import com.chy.agents.storage.relation.RelationStorage;
import com.chy.agents.storage.vector.VectorStoreConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 存储模块自动配置
 * 用于自动配置存储相关的组件
 */
@AutoConfiguration
@Import({
    VectorStoreConfig.class
})
@ComponentScan(basePackageClasses = {
    CacheStorage.class,
    RelationStorage.class
})
public class StorageAutoConfiguration {

    /**
     * 配置RedisTemplate
     */
    @Bean
    @ConditionalOnMissingBean(name = "agentsRedisTemplate")
    public RedisTemplate<String, Object> agentsRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 使用String序列化器处理key
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // 使用JSON序列化器处理value
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        template.afterPropertiesSet();
        return template;
    }
} 