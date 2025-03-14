# CHY Agents - 存储向量

该子模块为CHY Agents提供向量数据库集成，实现高效的语义搜索和相似度匹配能力。

## 支持的向量数据库

- **Milvus**: 为相似度搜索设计的高性能向量数据库
- **PGVector**: PostgreSQL的向量相似度搜索扩展
- **Weaviate**: 向量搜索引擎和知识图谱
- **Qdrant**: 具有扩展过滤功能的向量相似度搜索引擎
- **Chroma**: 为AI应用设计的开源嵌入式数据库
- **FAISS**: Facebook AI相似度搜索，用于高效的相似度搜索

## 核心特性

- **抽象接口**: 所有向量数据库实现的通用API
- **嵌入集成**: 与嵌入模型的无缝集成
- **高级过滤**: 除向量相似度外，还基于元数据过滤结果
- **混合搜索**: 结合向量相似度与基于关键词的过滤
- **批量操作**: 高效的批量插入和查询能力
- **文档管理**: 存储、检索和更新具有向量表示的文档

## 配置

### Milvus示例

```java
@Configuration
public class MilvusConfig {
    @Bean
    @ConditionalOnProperty(name = "chy.agents.storage.vector.type", havingValue = "milvus")
    public VectorStore milvusVectorStore(
            @Value("${chy.agents.storage.vector.milvus.host}") String host,
            @Value("${chy.agents.storage.vector.milvus.port}") int port,
            EmbeddingClient embeddingClient) {
        return MilvusVectorStore.builder()
                .embeddingClient(embeddingClient)
                .collectionName("documents")
                .connectionString("http://" + host + ":" + port)
                .build();
    }
}
```

## 使用示例

```java
@Service
public class VectorSearchService {
    private final VectorStore vectorStore;
    private final EmbeddingClient embeddingClient;
    
    public VectorSearchService(VectorStore vectorStore, EmbeddingClient embeddingClient) {
        this.vectorStore = vectorStore;
        this.embeddingClient = embeddingClient;
    }
    
    public void indexDocument(String id, String content, Map<String, Object> metadata) {
        Document document = Document.builder()
                .id(id)
                .content(content)
                .metadata(metadata)
                .build();
        
        vectorStore.add(Collections.singletonList(document));
    }
    
    public List<Document> search(String query, int topK, Map<String, Object> filterMetadata) {
        return vectorStore.similaritySearch(
            SearchRequest.builder()
                .query(query)
                .topK(topK)
                .filter(filterMetadata)
                .build()
        );
    }
} 