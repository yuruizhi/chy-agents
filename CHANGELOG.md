# 更新日志

遵循 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/) 规范

## [1.0.2-SNAPSHOT] - 2025-06-19
### 变更
- ⬆️ 升级 Spring AI 从 1.0.0-M6 到 1.0.0 正式版
- ⬆️ 升级 Spring AI Alibaba 从 1.0.0-M6.1 到 1.0.0.2 正式版
- ✨ 新增 Alibaba Graph 多智能体框架支持
- 🏗️ 新增 chy-agents-model-alibaba-graph 模块
- 🔧 完善依赖管理和仓库配置
- 🛠️ 更新系统架构与文档说明

## [1.0.1-SNAPSHOT] - 2025-03-12
### 新增
- ✨ 集成阿里云通义系列大模型（Qwen、Wanx）
- 🚀 支持多模型动态路由选择机制
- 🔒 新增企业级内容安全过滤组件
- 🏭 添加私有化部署配置支持

### 变更
- ⬆️ 升级Spring Boot至3.3.4
- ⬆️ 升级Spring AI至1.0.0-M6
- ♻️ 重构模型路由配置方式
- 🧠 优化内存管理实现

### 修复
- 🐛 解决EmbeddingClient包路径变更问题
- 🔧 修复阿里云SDK依赖坐标错误
- 🚑 修正流式响应处理逻辑
- 🧵 修复多线程环境下的会话管理问题

## [1.0.0-SNAPSHOT] - 2025-03-12
### 新增
- 🎉 初始版本发布
- 🤖 实现基础代理框架
- 💬 支持OpenAI模型集成
- 📚 完成RAG基础功能
- 🖼️ 实现多模态交互支持

### 已知问题
- ⚠️ 长上下文处理存在性能瓶颈
- ⚠️ 多模型切换时偶现会话丢失

---

[版本对比](https://github.com/yuruizhi/chy-agents/compare/v0.8.0...v1.0.0-M6) 