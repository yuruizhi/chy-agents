# CHY Agents - Storage

This module provides various storage capabilities for CHY Agents, including vector databases, relational databases, and caching mechanisms.

## Submodules

- **chy-agents-storage-vector**: Vector database integrations for semantic search (Milvus, PGVector, Weaviate, Qdrant, etc.)
- **chy-agents-storage-relation**: Relational database support for structured data storage (PostgreSQL, MySQL, Neo4j)
- **chy-agents-storage-cache**: Caching mechanisms for improved performance (Redis, Caffeine)

## Key Features

- **Flexible Storage Options**: Multiple storage solutions for different use cases
- **Unified API**: Common interface across different storage providers
- **Hybrid Search**: Combine vector and keyword search capabilities
- **Scalable Storage**: Solutions that can grow with your application
- **Persistent & Transient Options**: Both long-term and temporary storage solutions

## Usage

### Vector Storage Example

```java
@Service
public class RagService {
    @Resource
    private VectorStore vectorStore;
    @Resource
    private ChatClient chatClient;
    
    public String query(String question) {
        List<Document> docs = vectorStore.similaritySearch(
            SearchRequest.builder()
                .query(question)
                .topK(3)
                .build()
        );
        
        String context = docs.stream()
            .map(Document::getContent)
            .collect(Collectors.joining("\n"));
            
        return chatClient.call(new Prompt(
            "Based on the following information, answer the question: \n" + 
            context + "\nQuestion: " + question
        )).getResult().getOutput().getContent();
    }
}
``` 