# CHY Agents - 存储缓存

该子模块为CHY Agents提供缓存机制，实现高性能数据访问并减轻主数据源的负载。

## 支持的缓存解决方案

- **Redis**: 用于分布式缓存的内存数据结构存储
- **Caffeine**: Java的高性能、近乎最优的缓存库
- **Spring缓存抽象**: 跨不同提供商的统一缓存接口

## 核心特性

- **分层缓存**: 结合本地和分布式缓存策略
- **TTL管理**: 可配置的缓存数据生存时间设置
- **缓存淘汰**: 基于访问模式的智能淘汰策略
- **并发访问**: 多用户环境下的线程安全操作
- **缓存统计**: 缓存性能的监控和指标
- **序列化选项**: 灵活的序列化/反序列化机制

## 配置

### Redis缓存示例

```java
@Configuration
@EnableCaching
public class RedisCacheConfig {
    @Bean
    @ConditionalOnProperty(name = "chy.agents.storage.cache.type", havingValue = "redis")
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .disableCachingNullValues()
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
                
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();
    }
}
```

### Caffeine缓存示例

```java
@Configuration
@EnableCaching
public class CaffeineCacheConfig {
    @Bean
    @ConditionalOnProperty(name = "chy.agents.storage.cache.type", havingValue = "caffeine")
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .maximumSize(1000)
                .recordStats());
                
        return cacheManager;
    }
}
```

## 使用示例

### 缓存服务示例

```java
@Service
public class EmbeddingCacheService {
    private final CacheManager cacheManager;
    private final EmbeddingClient embeddingClient;
    
    // 缓存名称常量
    private static final String EMBEDDING_CACHE = "embeddings";
    
    public EmbeddingCacheService(CacheManager cacheManager, EmbeddingClient embeddingClient) {
        this.cacheManager = cacheManager;
        this.embeddingClient = embeddingClient;
    }
    
    @Cacheable(value = EMBEDDING_CACHE, key = "#text")
    public List<Double> getEmbedding(String text) {
        // 只有当结果不在缓存中时才会调用此方法
        return embeddingClient.embed(text);
    }
    
    public void invalidateCache(String text) {
        Cache cache = cacheManager.getCache(EMBEDDING_CACHE);
        if (cache != null) {
            cache.evict(text);
        }
    }
    
    public void clearAllCaches() {
        Cache cache = cacheManager.getCache(EMBEDDING_CACHE);
        if (cache != null) {
            cache.clear();
        }
    }
} 