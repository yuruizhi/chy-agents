# CHY Agents - Storage Cache

This submodule provides caching mechanisms for CHY Agents, enabling high-performance data access and reducing load on primary data sources.

## Supported Caching Solutions

- **Redis**: In-memory data structure store for distributed caching
- **Caffeine**: High-performance, near-optimal caching library for Java
- **Spring Cache Abstraction**: Unified caching interface across different providers

## Key Features

- **Tiered Caching**: Combine local and distributed caching strategies
- **TTL Management**: Configurable time-to-live settings for cached data
- **Cache Eviction**: Intelligent eviction policies based on access patterns
- **Concurrent Access**: Thread-safe operations for multi-user environments
- **Cache Statistics**: Monitoring and metrics for cache performance
- **Serialization Options**: Flexible serialization/deserialization mechanisms

## Configuration

### Redis Cache Example

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

### Caffeine Cache Example

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

## Usage

### Caching Service Example

```java
@Service
public class EmbeddingCacheService {
    private final CacheManager cacheManager;
    private final EmbeddingClient embeddingClient;
    
    // Cache name constants
    private static final String EMBEDDING_CACHE = "embeddings";
    
    public EmbeddingCacheService(CacheManager cacheManager, EmbeddingClient embeddingClient) {
        this.cacheManager = cacheManager;
        this.embeddingClient = embeddingClient;
    }
    
    @Cacheable(value = EMBEDDING_CACHE, key = "#text")
    public List<Double> getEmbedding(String text) {
        // This method will only be called if the result is not in the cache
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