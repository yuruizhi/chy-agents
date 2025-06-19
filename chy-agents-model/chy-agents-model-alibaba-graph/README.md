# CHY Agents - Model Alibaba Graph

基于Spring AI Alibaba 1.0.0.2的Graph多智能体框架集成模块。

## 功能特性

* 基于Spring AI Alibaba Graph实现多智能体协作
* 支持状态图(StateGraph)和工作流(Workflow)的构建和执行
* 提供灵活的节点和边的定义方式
* 支持多种LLM节点类型，便于构建复杂的智能体系统

## 使用示例

### 1. 基本配置

在application.yml中启用Spring AI Alibaba Graph:

```yaml
spring:
  ai:
    alibaba.graph:
      enabled: true
      sample-workflow: true  # 启用示例工作流
```

### 2. 创建简单工作流

```java
@Bean
public StateGraph createWorkflow(StateGraphFactory stateGraphFactory) {
    return new StateGraph("My Workflow", stateGraphFactory)
        // 添加节点
        .addNode("start", StateGraphFactory.node(() -> "开始处理"))
        .addNode("process", StateGraphFactory.node(() -> "处理数据"))
        .addNode("end", StateGraphFactory.node(() -> "完成处理"))
        // 添加边
        .addEdge("start", "process")
        .addEdge("process", "end");
}
```

### 3. 创建LLM多智能体系统

```java
@Bean
public StateGraph createResearchAssistant(
        StateGraphFactory stateGraphFactory,
        ChatClient planningClient, 
        ChatClient researchClient, 
        ChatClient summaryClient) {
    
    return new StateGraph("Research Assistant", stateGraphFactory)
        // 规划智能体
        .addNode("planner", StateGraphFactory.llmNode(planningClient, 
            "将用户请求分解为研究计划"))
        // 研究智能体
        .addNode("researcher", StateGraphFactory.llmNode(researchClient, 
            "执行深度研究"))
        // 总结智能体
        .addNode("summarizer", StateGraphFactory.llmNode(summaryClient, 
            "总结研究发现并给出建议"))
        // 构建工作流
        .addEdge("planner", "researcher")
        .addEdge("researcher", "summarizer");
}
```

### 4. 执行工作流

```java
// 编译工作流
Workflow workflow = myStateGraph.compile();

// 准备上下文参数
Map<String, Object> context = new HashMap<>();
context.put("input", "用户输入内容");

// 执行工作流
workflowExecutor.execute(workflow, context)
    .thenAccept(result -> {
        System.out.println("执行结果: " + result);
    });
```

## 高级用法

1. **条件边**：使用条件边控制执行流程

```java
.addEdge("analyzer", "high_priority", (input, output) -> 
    output.toString().contains("高优先级"))
.addEdge("analyzer", "low_priority", (input, output) -> 
    !output.toString().contains("高优先级"))
```

2. **自定义节点**：创建复杂的处理节点

```java
.addNode("custom_node", StateGraphFactory.node(context -> {
    // 自定义逻辑处理
    String input = (String) context.get("input");
    // 执行处理...
    return "处理结果";
}))
```

3. **并行节点**：创建并行执行的节点

```java
.addNode("parallel_process", StateGraphFactory.parallelNode(Arrays.asList(
    // 并行节点列表
    StateGraphFactory.node(() -> "并行处理1"),
    StateGraphFactory.node(() -> "并行处理2"),
    StateGraphFactory.node(() -> "并行处理3")
)))
```

## 与CHY Agents集成

本模块可以与CHY Agents的Agent、Router和工具系统无缝集成，提供更灵活的智能体构建能力。