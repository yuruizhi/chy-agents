package com.chy.agents.storage.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 缓存存储接口
 * 用于临时存储数据，提高系统性能
 */
public interface CacheStorage {

    /**
     * 存储值
     *
     * @param key 键
     * @param value 值
     * @return 是否成功
     */
    boolean put(String key, Object value);

    /**
     * 存储值，带过期时间
     *
     * @param key 键
     * @param value 值
     * @param ttl 过期时间(秒)
     * @return 是否成功
     */
    boolean put(String key, Object value, long ttl);

    /**
     * 获取值
     *
     * @param key 键
     * @param type 值类型
     * @return 值
     */
    <T> Optional<T> get(String key, Class<T> type);

    /**
     * 删除值
     *
     * @param key 键
     * @return 是否成功
     */
    boolean delete(String key);

    /**
     * 内存实现
     */
    @Component
    @ConditionalOnProperty(prefix = "chy.agents.storage.cache", name = "type", havingValue = "memory", matchIfMissing = true)
    class InMemoryCacheStorage implements CacheStorage {
        
        private final Map<String, Object> cache = new ConcurrentHashMap<>();
        private final Map<String, Long> expiry = new ConcurrentHashMap<>();

        @Override
        public boolean put(String key, Object value) {
            cache.put(key, value);
            expiry.remove(key);
            return true;
        }

        @Override
        public boolean put(String key, Object value, long ttl) {
            cache.put(key, value);
            expiry.put(key, System.currentTimeMillis() + (ttl * 1000));
            return true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> Optional<T> get(String key, Class<T> type) {
            // 检查是否过期
            Long expiryTime = expiry.get(key);
            if (expiryTime != null && System.currentTimeMillis() > expiryTime) {
                cache.remove(key);
                expiry.remove(key);
                return Optional.empty();
            }
            
            Object value = cache.get(key);
            if (value == null) {
                return Optional.empty();
            }
            
            if (type.isInstance(value)) {
                return Optional.of((T) value);
            }
            
            return Optional.empty();
        }

        @Override
        public boolean delete(String key) {
            cache.remove(key);
            expiry.remove(key);
            return true;
        }
    }

    /**
     * Redis实现
     */
    @Component
    @ConditionalOnBean(RedisTemplate.class)
    @ConditionalOnProperty(prefix = "chy.agents.storage.cache", name = "type", havingValue = "redis")
    class RedisCacheStorage implements CacheStorage {
        
        private final RedisTemplate<String, Object> redisTemplate;
        
        public RedisCacheStorage(RedisTemplate<String, Object> redisTemplate) {
            this.redisTemplate = redisTemplate;
        }

        @Override
        public boolean put(String key, Object value) {
            redisTemplate.opsForValue().set(key, value);
            return true;
        }

        @Override
        public boolean put(String key, Object value, long ttl) {
            redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
            return true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> Optional<T> get(String key, Class<T> type) {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return Optional.empty();
            }
            
            if (type.isInstance(value)) {
                return Optional.of((T) value);
            }
            
            return Optional.empty();
        }

        @Override
        public boolean delete(String key) {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        }
    }
} 