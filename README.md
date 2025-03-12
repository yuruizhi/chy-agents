# CHY Agents - 基于Spring AI的智能代理系统

CHY Agents是一个基于Spring AI构建的分布式智能代理系统，支持多模态交互、知识检索与推理、工作流编排及模型评估。系统采用模块化设计，提供灵活可扩展的AI应用开发框架。

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![JDK](https://img.shields.io/badge/JDK-17%2B-green.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-0.8.0-orange.svg)](https://spring.io/projects/spring-ai)
[![Alibaba Cloud](https://img.shields.io/badge/Alibaba_Cloud-1.0.0-blue.svg)](https://www.aliyun.com/)

## 新增功能亮点 ✨

**阿里云模型深度集成**：
- 通义千问大语言模型支持
- 通义万相图像生成模型
- 灵积向量计算服务
- 内容安全审核服务

**企业级特性**：
- 私有化部署支持
- 行业知识增强
- 商业版权保障
- 全链路审计追踪

## 快速接入阿里云

### 配置示例
```yaml
spring:
  ai:
    alibaba:
      access-key-id: ${ALIBABA_ACCESS_KEY}
      access-key-secret: ${ALIBABA_SECRET_KEY}
      region: cn-hangzhou
      chat:
        model: qwen-max
        endpoint: https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation
```

### 代码示例
```java
// 创建使用阿里云模型的代理
@Bean
@Qualifier("alibabaAgent")
public Agent alibabaAgent(
    @Qualifier("alibabaChatClient") ChatClient chatClient) {
    return new SimpleAgent("阿里云助手", "基于通义千问的智能助手", chatClient);
}

// 调用示例
String response = agent.execute("杭州亚运会的吉祥物是什么？", "alibaba");
```

## 架构升级说明

### 新增模块
```
chy-agents/
└── chy-agents-alibaba     # 阿里云模型集成
    ├── client/            # 客户端适配
    ├── config/            # 配置管理
    └── filter/            # 安全过滤
```

### 模型路由机制
```java
// 根据provider参数动态选择模型
public String execute(String input, String provider) {
    ChatClient client = modelRouter.selectClient(provider);
    return client.call(prompt);
}
```

完整更新内容请查看[更新日志](CHANGELOG.md)

## 项目架构

```
chy-agents/
├── chy-agents-common       # 公共组件和工具类
├── chy-agents-core         # 核心代理框架
├── chy-agents-chat         # 对话能力模块
├── chy-agents-rag          # 检索增强生成模块
├── chy-agents-image        # 图像处理模块
├── chy-agents-rest         # REST API接口模块
```

### 计划模块
```
chy-agents/
├── chy-agents-workflow     # AI工作流和编排模块 
├── chy-agents-evaluation   # AI响应评估和监控模块
├── chy-agents-fine-tuning  # 模型微调和训练模块
├── chy-agents-multimodal   # 多模态内容处理模块
```

## 核心功能

### 代理系统
- **多代理协作框架**：基于`SimpleAgent`实现的可扩展代理系统
- **工具集成机制**：通过`ToolRegistry`管理和动态注册工具
- **会话记忆管理**：使用`SessionManager`维护长上下文对话历史

### 自然语言处理
- **流式响应处理**：`StreamingChatService`实现的低延迟响应机制
- **WebSocket实时通信**：支持前端实时显示生成内容
- **上下文理解**：支持多轮对话理解与记忆

### 多模态处理
- **图像分析**：`ImageAnalysisService`支持视觉识别与理解
- **多模态融合**：结合文本和图像输入的综合分析

### 检索增强生成(RAG)
- **内容分块策略**：`TextChunkingStrategy`实现的文档智能分块
- **语义检索**：基于向量数据库的相似内容检索
- **知识整合**：检索结果与LLM生成内容无缝融合

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.8+
- Redis (可选，用于缓存)
- Spring AI支持的LLM服务账号

### 配置API密钥
在`application.properties`或`application.yml`中配置您的AI服务提供商API密钥：

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      # 可选：自定义API基础URL
      base-url: ${OPENAI_BASE_URL:https://api.openai.com}
      # 可选：默认模型配置  
      chat-model: gpt-4o
      embedding-model: text-embedding-3-large
```

### 构建与运行

```bash
# 克隆仓库
git clone https://github.com/your-username/chy-agents.git
cd chy-agents

# 编译项目
mvn clean install

# 运行服务
cd chy-agents-rest
mvn spring-boot:run
```

### API文档
启动后访问：http://localhost:8080/swagger-ui.html

## 模块详解

### chy-agents-core
核心代理框架，定义了Agent接口和SimpleAgent实现，提供了代理的基础能力。

```java
// 创建一个简单代理示例
ChatClient chatClient = ... // 通过Spring注入
SimpleAgent agent = new SimpleAgent("助手", "一个通用AI助手", chatClient);

// 注册工具
agent.setTools(Arrays.asList(
    new Calculator(), 
    new WebSearchTool()
));

// 执行指令
String response = agent.execute("帮我计算今年的营业增长率，去年销售额是100万，今年是120万");
```

### chy-agents-chat
提供聊天对话能力，包括会话管理和流式响应。

```java
// 流式聊天示例
@Controller
public class ChatController {
    @Autowired
    private StreamingChatService chatService;
    
    @GetMapping("/chat")
    public Flux<String> chat(@RequestParam String message) {
        return chatService.chatStream(message, Collections.emptyList());
    }
}
```

### chy-agents-rag
实现基于检索增强的生成能力，提高回答的准确性和知识覆盖面。

```java
// 文档分块示例
Document document = new Document("这是一篇很长的文档...");
ChunkingStrategy chunkingStrategy = new TextChunkingStrategy();
List<Document> chunks = chunkingStrategy.chunk(document, 1000, 100);
```

### chy-agents-image
提供图像处理和分析能力，支持视觉智能应用。

```java
// 图像分析示例
@Service
public class ImageService {
    @Autowired
    private ImageAnalysisService imageAnalysisService;
    
    public String analyzeImage(MultipartFile file, String prompt) throws IOException {
        return imageAnalysisService.analyzeImage(
            file.getBytes(), 
            prompt.isEmpty() ? "描述这张图片" : prompt
        );
    }
}
```

## 扩展开发

### 创建自定义代理

```java
public class CustomerSupportAgent extends SimpleAgent {
    public CustomerSupportAgent(ChatClient chatClient) {
        super("客服助手", "专业的客户服务AI代理", chatClient);
        
        // 添加特定于客服领域的工具
        this.setTools(Arrays.asList(
            new OrderLookupTool(),
            new RefundProcessTool()
        ));
    }
    
    @Override
    protected String generateSystemPrompt() {
        return "你是一名专业的客服代表，帮助解决客户的问题。" +
               "你可以查询订单、处理退款等。" +
               "请保持礼貌和专业。";
    }
}
```

### 注册自定义工具

```java
@Component
public class WeatherTool implements Agent.Tool {
    @Override
    public String getName() {
        return "weather";
    }
    
    @Override
    public String getDescription() {
        return "获取指定城市的天气信息，参数格式：{\"city\":\"北京\"}";
    }
    
    @Override
    public String execute(String input) {
        // 解析参数并调用天气API
        JsonNode params = new ObjectMapper().readTree(input);
        String city = params.get("city").asText();
        // 调用天气API并返回结果
        return "北京今天晴朗，气温25℃，东北风2级";
    }
}
```

## 部署方案

### Docker部署

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

```bash
docker build -t chy-agents .
docker run -p 8080:8080 -e OPENAI_API_KEY=your_key_here chy-agents
```

### Kubernetes部署
提供了基础的K8s配置文件，支持水平扩展和负载均衡：

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: chy-agents
spec:
  replicas: 3
  selector:
    matchLabels:
      app: chy-agents
  template:
    metadata:
      labels:
        app: chy-agents
    spec:
      containers:
      - name: chy-agents
        image: chy-agents:latest
        ports:
        - containerPort: 8080
        env:
        - name: OPENAI_API_KEY
          valueFrom:
            secretKeyRef:
              name: ai-secrets
              key: openai-api-key
```

## 性能优化

### 响应缓存
实现了基于Redis的响应缓存机制，减少重复查询的API调用：

```java
@Cacheable(value = "aiResponses", key = "#prompt.hashCode()")
public String generateResponse(Prompt prompt) {
    return chatClient.call(prompt).getResult().getOutput().getContent();
}
```

### 批量处理
支持请求合并和批量处理，提高处理效率：

```java
@Scheduled(fixedRate = 500)
public void processBatch() {
    List<ChatRequest> batch = pendingRequests.drain(50);
    if (!batch.isEmpty()) {
        Map<String, String> results = llmClient.batchProcess(batch);
        // 分发结果
    }
}
```

## 未来计划

1. **chy-agents-workflow**：工作流定义、执行与编排
2. **chy-agents-evaluation**：AI响应质量评估与监控
3. **chy-agents-fine-tuning**：模型训练与微调管理
4. **chy-agents-multimodal**：多模态内容处理增强

## 贡献指南

我们欢迎社区贡献！请参阅[贡献指南](CONTRIBUTING.md)了解如何参与项目开发。

## 许可证

本项目采用 [Apache License 2.0](LICENSE) 许可证

## 作者

**YuRuizhi** - 282373647@qq.com

---

*CHY Agents - 让AI与业务场景无缝融合*

## 更新日志

### [1.0.1-SNAPSHOT]] - 2025-03-12
#### 新增
- 集成阿里云通义系列大模型（Qwen、Wanx）
- 支持多模型动态路由选择机制
- 新增企业级内容安全过滤组件

#### 变更
- 升级Spring Boot至3.3.4
- 重构模型路由配置方式

### [1.0.0-SNAPSHOT] - 2025-03-12
#### 初始版本
- 实现基础代理框架
- 支持OpenAI模型集成

完整更新内容请查看[更新日志详情](CHANGELOG.md)