# CHY Agents - 企业级智能代理系统

CHY Agents 是一个基于 Spring AI 构建的企业级智能代理系统，支持多模型路由、多模态交互、知识增强检索、内容安全审核等特性。系统采用模块化设计，提供灵活可扩展的 AI 应用开发框架。

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![JDK](https://img.shields.io/badge/JDK-17%2B-green.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0--M5-orange.svg)](https://spring.io/projects/spring-ai)
[![Spring AI Alibaba](https://img.shields.io/badge/Spring%20AI%20Alibaba-1.0.0--M5.1-blue.svg)](https://www.aliyun.com/)

## 核心特性

### 1. 多模型智能路由
- 支持主流模型：OpenAI、阿里云、DeepSeek、Claude、Gemini
- 私有模型集成：llama.cpp、vllm 等
- MoE (Mixture of Experts) 路由策略
- CoE (Chain of Experts) 协作机制
- 智能负载均衡与故障转移

### 2. 增强能力支持
- Memory 系统：短期记忆、长期记忆
- Function Calling：工具注册、调用链、错误处理
- 对话上下文：会话管理、状态追踪
- 知识图谱：关系存储、推理能力

### 3. 多模态处理
- 图像：DALL-E、SD、通义万相生成，GPT-4V/Qwen-VL 理解
- 音频：Whisper、通义听悟识别，Azure/阿里云语音合成
- 视频：视频分析、场景识别、视频生成
- 多模态融合：跨模态理解与生成

### 4. 企业级存储
- 向量数据库：Milvus、Weaviate、Qdrant、Pinecone
- 关系数据库：PostgreSQL、MySQL 向量扩展
- 内存数据库：Redis、FAISS、Annoy
- 混合检索：语义重排、过滤增强

### 5. 应用增强
- 插件系统：数据源、处理器、输出插件
- 工作流引擎：BPMN、自定义DSL
- 知识库管理：文档处理、版本控制
- 场景模板：快速部署、参数配置

## 系统架构

```
chy-agents/
├── chy-agents-core         # 核心框架
│   ├── agent/              # 代理定义
│   ├── router/             # 模型路由
│   │   ├── moe/            # 专家混合
│   │   ├── coe/            # 专家链
│   │   └── lb/             # 负载均衡
│   └── evaluation/         # 评估框架
├── chy-agents-common       # 公共组件
│   ├── config/             # 配置管理
│   └── utils/              # 工具类
├── chy-agents-memory       # 记忆系统
│   ├── short/              # 短期记忆
│   ├── long/               # 长期记忆
│   └── graph/              # 知识图谱
├── chy-agents-function     # 函数调用
│   ├── registry/           # 工具注册
│   ├── chain/              # 调用链
│   └── error/              # 错误处理
├── chy-agents-chat         # 对话模块
│   ├── core                # 记忆管理
│   └── streaming           # 流式处理
├── chy-agents-rag          # 知识检索
│   ├── chunk/              # 文档分块
│   ├── vector/             # 向量存储
│   └── search/             # 检索引擎
├── chy-agents-multimodal   # 多模态处理
│   ├── chy-agents-multimodal-image    # 图像处理
│   ├── chy-agents-multimodal-audio    # 音频处理
│   ├── chy-agents-multimodal-video    # 视频处理
│   └── chy-agents-multimodal-fusion   # 模态融合
├── chy-agents-model        # 模型集成
│   ├── chy-agents-model-openai     # OpenAI
│   ├── chy-agents-model-alibaba    # 阿里云
│   ├── chy-agents-model-deepseek   # DeepSeek
│   ├── chy-agents-model-anthropic  # Claude
│   ├── chy-agents-model-google     # Gemini
│   └── chy-agents-model-private    # 私有模型
├── chy-agents-storage      # 存储模块
│   ├── chy-agents-storage-vector   # 向量存储
│   ├── chy-agents-storage-relation # 关系存储
│   └── chy-agents-storage-cache    # 缓存存储
├── chy-agents-plugin       # 插件系统
│   ├── sdk/                # 插件SDK
│   ├── loader/             # 插件加载
│   └── manager/            # 插件管理
├── chy-agents-workflow     # 工作流引擎
│   ├── definition/         # 流程定义
│   ├── execution/          # 流程执行
│   └── monitor/            # 流程监控
├── chy-agents-security     # 安全模块
│   ├── audit/              # 内容审核
│   └── filter/             # 敏感词过滤
└── chy-agents-rest         # API接口
    ├── controller/         # 接口控制
    └── docs/               # API文档
```

## 应用场景

### 1. 企业知识库
- 多源数据接入与处理
- 智能问答与推荐
- 知识图谱构建
- 权限与审计

### 2. 智能客服
- 多轮对话管理
- 意图识别分类
- 情感分析
- 人工协作

### 3. 内容生成
- 营销文案
- 图片设计
- 视频剪辑
- 质量控制

### 4. 数据分析
- 数据处理
- 可视化
- 报告生成
- 见解发现

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.8+
- PostgreSQL 14+ (可选，用于向量存储)
- Redis 6+ (可选，用于缓存)

### 配置示例

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat-model: gpt-4
      embedding-model: text-embedding-3-small
    alibaba:
      access-key-id: ${ALIBABA_ACCESS_KEY}
      access-key-secret: ${ALIBABA_SECRET_KEY}
      region: cn-hangzhou
      dashscope:
        api-key: ${ALIBABA_API_KEY}
        model: qwen-max
```

### 代码示例

1. 多模型路由：
```java
@Service
public class ChatService {
    @Resource
    private ModelRouter modelRouter;
    
    public String chat(String input, String provider) {
        ChatClient client = modelRouter.selectClient(provider);
        return client.call(new Prompt(input))
                    .getResult()
                    .getOutput()
                    .getContent();
    }
}
```

2. RAG 检索：
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
            "基于以下信息回答问题：\n" + context + "\n问题：" + question
        )).getResult().getOutput().getContent();
    }
}
```

3. 图像生成：
```java
@Service
public class ImageService {
    @Resource
    private ImageClient imageClient;
    
    public List<Image> generateImages(String prompt) {
        return imageClient.call(new ImagePrompt(prompt))
                         .getResult()
                         .getOutput();
    }
}
```

## 部署方案

### Docker Compose
```yaml
version: '3.8'
services:
  app:
        image: chy-agents:latest
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - OPENAI_API_KEY=${OPENAI_API_KEY}
      - ALIBABA_API_KEY=${ALIBABA_API_KEY}
        ports:
      - "8080:8080"
    depends_on:
      - postgres
      - redis
      
  postgres:
    image: postgres:14
    environment:
      - POSTGRES_DB=aiagents
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
    volumes:
      - pgdata:/var/lib/postgresql/data
      
  redis:
    image: redis:6
    volumes:
      - redisdata:/data

volumes:
  pgdata:
  redisdata:
```

### Kubernetes
提供了完整的 K8s 配置，支持：
- 自动扩缩容
- 负载均衡
- 健康检查
- 配置管理
- 密钥管理

## 性能优化

### 1. 响应优化
- 流式处理
- 结果缓存
- 批量处理

### 2. 成本控制
- Token 计数
- 模型选择
- 缓存策略

### 3. 可用性保障
- 熔断降级
- 限流保护
- 故障转移

## 安全特性

### 1. 内容安全
- 敏感词过滤
- 内容审核
- 行为检测

### 2. 访问控制
- 身份认证
- 权限管理
- 操作审计

## 开发计划

### 1.0.x
- [x] 基础框架搭建
- [x] OpenAI 集成
- [x] 阿里云集成
- [ ] RAG 基础能力
- [ ] 图像处理支持

### 1.1.x
- [ ] 工作流引擎
- [ ] 评估框架
- [ ] 多模态增强
- [ ] 知识库管理
- [ ] 性能优化

### 1.2.x
- [ ] 私有化部署
- [ ] 监控面板
- [ ] 插件系统
- [ ] 场景模板
- [ ] 开发者工具

## 贡献指南

我们欢迎社区贡献！请参阅[贡献指南](CONTRIBUTING.md)了解如何参与项目开发。

## 许可证

本项目采用 [Apache License 2.0](LICENSE) 许可证

## 作者

**YuRuizhi** - 282373647@qq.com

---

*CHY Agents - 让AI与业务场景无缝融合*


