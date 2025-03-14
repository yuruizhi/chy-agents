# CHY Agents - Storage Vector

This submodule provides vector database integrations for CHY Agents, enabling efficient semantic search and similarity matching capabilities.

## Supported Vector Databases

- **Milvus**: High-performance vector database designed for similarity search
- **PGVector**: PostgreSQL extension for vector similarity search
- **Weaviate**: Vector search engine and knowledge graph
- **Qdrant**: Vector similarity search engine with extended filtering capabilities
- **Chroma**: Open-source embedding database designed for AI applications
- **FAISS**: Facebook AI Similarity Search for efficient similarity search

## Key Features

- **Abstracted Interface**: Common API for all vector database implementations
- **Embedding Integration**: Seamless integration with embedding models
- **Advanced Filtering**: Filter results based on metadata in addition to vector similarity
- **Hybrid Search**: Combine vector similarity with keyword-based filtering
- **Batch Operations**: Efficient batch insertion and querying capabilities
- **Document Management**: Store, retrieve, and update documents with vector representations

## Configuration

### Milvus Example

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

## Usage

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