package com.chy.agents.model.alibaba.graph.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.StateGraphFactory;
import com.alibaba.cloud.ai.graph.WorkflowExecutor;

/**
 * Alibaba Graph 配置类
 * 
 * @author YuRuizhi
 * @date 2025/06/19
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.alibaba.graph", name = "enabled", havingValue = "true", matchIfMissing = false)
public class AlibabaGraphConfig {

    @Autowired(required = false)
    @Qualifier("openAiChatClient")
    private ChatClient openAiChatClient;
    
    @Autowired(required = false)
    @Qualifier("alibabaChatClient")
    private ChatClient alibabaChatClient;

    /**
     * 创建状态图工厂
     * 
     * @return StateGraphFactory 状态图工厂实例
     */
    @Bean
    @ConditionalOnMissingBean
    public StateGraphFactory stateGraphFactory() {
        return new StateGraphFactory();
    }
    
    /**
     * 创建工作流执行器
     * 
     * @return WorkflowExecutor 工作流执行器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public WorkflowExecutor workflowExecutor() {
        return new WorkflowExecutor();
    }
    
    /**
     * 研究型ChatClient，适合深度分析任务
     */
    @Bean
    @ConditionalOnMissingBean(name = "researchChatClient")
    public ChatClient researchChatClient() {
        // 优先使用阿里模型
        return alibabaChatClient != null ? alibabaChatClient : openAiChatClient;
    }
    
    /**
     * 总结型ChatClient，适合信息整合任务
     */
    @Bean
    @ConditionalOnMissingBean(name = "summaryChatClient")
    public ChatClient summaryChatClient() {
        // 优先使用OpenAI模型
        return openAiChatClient != null ? openAiChatClient : alibabaChatClient;
    }
    
    /**
     * 规划型ChatClient，适合任务分解和规划
     */
    @Bean
    @ConditionalOnMissingBean(name = "planningChatClient")
    public ChatClient planningChatClient() {
        // 优先使用OpenAI模型
        return openAiChatClient != null ? openAiChatClient : alibabaChatClient;
    }
    
    /**
     * 创建示例状态图 - 简单工作流
     * 
     * @param stateGraphFactory 状态图工厂
     * @return 状态图实例
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring.ai.alibaba.graph", name = "sample-workflow", havingValue = "true", matchIfMissing = false)
    public StateGraph sampleWorkflow(StateGraphFactory stateGraphFactory) {
        return new StateGraph("Sample Workflow", stateGraphFactory)
            // 添加节点和边的示例
            .addNode("start_node", StateGraphFactory.node(() -> "Start processing"))
            .addNode("process_node", StateGraphFactory.node(() -> "Processing data"))
            .addNode("end_node", StateGraphFactory.node(() -> "Finished processing"))
            .addEdge("start_node", "process_node")
            .addEdge("process_node", "end_node");
    }
}