# 智能代理系统

基于Spring AI构建的多功能AI代理系统，提供对话、图像处理、知识增强生成等AI能力。

## 项目结构

```
chy-agents
├── chy-agents-chat - 聊天功能模块
├── chy-agents-image - 图像处理模块
├── chy-agents-rag - RAG（检索增强生成）模块
├── chy-agents-common - 公共工具类模块
├── chy-agents-core - 核心业务逻辑模块
└── chy-agents-rest - REST API接口模块
```

## 技术栈

- **基础框架**: Spring Boot 3.2.4
- **AI支持**: Spring AI 1.0.0-SNAPSHOT
- **JDK版本**: 17
- **构建工具**: Maven
- **编码规范**: UTF-8

## 功能特性

- 多模态AI能力集成
- 模块化架构设计
- RESTful API接口
- 检索增强生成(RAG)支持
- 统一依赖管理

## 构建运行

```bash
mvn clean install
# 运行具体模块示例：
mvn spring-boot:run -pl chy-agents-rest
```

## 环境要求

1. JDK 17+
2. Maven 3.9+
3. Spring AI API密钥（配置于application.yml）

## 仓库配置

```xml
<!-- 国内镜像加速 -->
<repository>
    <id>aliyun-maven</id>
    <url>https://maven.aliyun.com/repository/public</url>
</repository>

<!-- Spring Snapshots仓库 -->
<repository>
    <id>spring-snapshots</id>
    <url>https://repo.spring.io/snapshot</url>
</repository>
```