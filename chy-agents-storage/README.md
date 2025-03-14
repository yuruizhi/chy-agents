# CHY Agents - 存储模块

该模块为CHY Agents提供各种存储能力，包括向量数据库、关系型数据库和缓存机制。

## 子模块

- **chy-agents-storage-vector**: 向量数据库集成，用于语义搜索（Milvus、PGVector、Weaviate、Qdrant等）
- **chy-agents-storage-relation**: 关系型数据库支持，用于结构化数据存储（PostgreSQL、MySQL、Neo4j）
- **chy-agents-storage-cache**: 缓存机制，用于提高性能（Redis、Caffeine）

## 核心特性

- **灵活存储选项**: 针对不同用例的多种存储解决方案
- **统一API**: 跨不同存储提供商的通用接口
- **混合搜索**: 结合向量搜索和关键词搜索能力
- **可扩展存储**: 可随应用增长的解决方案
- **持久化与临时选项**: 同时提供长期和临时存储解决方案

## 使用示例

### 向量存储示例

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
            "基于以下信息回答问题: \n" + 
            context + "\n问题: " + question
        )).getResult().getOutput().getContent();
    }
} 