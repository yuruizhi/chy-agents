package com.chy.agents.model.alibaba.graph.service;

import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.StateGraphFactory;
import com.alibaba.cloud.ai.graph.Workflow;
import com.alibaba.cloud.ai.graph.WorkflowExecutor;
import com.chy.agents.core.agent.Agent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 多智能体服务，基于Spring AI Alibaba Graph框架
 * 
 * @author YuRuizhi
 * @date 2025/06/19
 */
@Service
@ConditionalOnProperty(prefix = "spring.ai.alibaba.graph", name = "enabled", havingValue = "true")
public class MultiAgentService {

    @Autowired
    private StateGraphFactory stateGraphFactory;
    
    @Autowired(required = false)
    @Qualifier("researchChatClient")
    private ChatClient researchChatClient;
    
    @Autowired(required = false)
    @Qualifier("summaryChatClient")
    private ChatClient summaryChatClient;
    
    @Autowired(required = false)
    @Qualifier("planningChatClient")
    private ChatClient planningChatClient;
    
    @Autowired(required = false)
    private WorkflowExecutor workflowExecutor;
    
    /**
     * 创建研究助手多智能体系统
     * 
     * @return 状态图实例
     */
    public StateGraph createResearchAssistant() {
        // 创建包含多个专家智能体的图
        return new StateGraph("Research Assistant", stateGraphFactory)
            // 规划智能体，负责分解任务
            .addNode("planner", StateGraphFactory.llmNode(planningChatClient, "将用户请求分解为研究计划"))
            // 研究智能体，负责深入分析
            .addNode("researcher", StateGraphFactory.llmNode(researchChatClient, "执行深度研究"))
            // 总结智能体，负责整合信息
            .addNode("summarizer", StateGraphFactory.llmNode(summaryChatClient, "总结研究发现并给出建议"))
            // 构建工作流
            .addEdge("planner", "researcher")
            .addEdge("researcher", "summarizer");
    }
    
    /**
     * 创建代码评审多智能体系统
     * 
     * @return 状态图实例
     */
    public StateGraph createCodeReviewAssistant() {
        return new StateGraph("Code Review Assistant", stateGraphFactory)
            .addNode("analyzer", StateGraphFactory.llmNode(researchChatClient, "分析代码结构和质量"))
            .addNode("security_checker", StateGraphFactory.llmNode(planningChatClient, "检查安全漏洞"))
            .addNode("performance_reviewer", StateGraphFactory.llmNode(researchChatClient, "评估性能问题"))
            .addNode("reporter", StateGraphFactory.llmNode(summaryChatClient, "生成综合评审报告"))
            .addEdge("analyzer", "security_checker")
            .addEdge("analyzer", "performance_reviewer") 
            .addEdge("security_checker", "reporter")
            .addEdge("performance_reviewer", "reporter");
    }
    
    /**
     * 执行多智能体工作流
     * 
     * @param graph 状态图
     * @param input 用户输入
     * @return 执行结果
     */
    public CompletableFuture<String> executeWorkflow(StateGraph graph, String input) {
        if (workflowExecutor == null) {
            throw new IllegalStateException("WorkflowExecutor未配置");
        }
        
        Map<String, Object> context = new HashMap<>();
        context.put("input", input);
        context.put("startTime", System.currentTimeMillis());
        
        Workflow workflow = graph.compile();
        
        return workflowExecutor.execute(workflow, context)
            .thenApply(result -> {
                if (result.containsKey("output")) {
                    return (String) result.get("output");
                }
                return "执行完成，但没有返回输出内容";
            });
    }
}