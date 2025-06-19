package com.chy.agents.model.alibaba.graph.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.StateGraphFactory;

/**
 * Alibaba Graph 配置类
 * 
 * @author YuRuizhi
 * @date 2024/06/15
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.alibaba.graph", name = "enabled", havingValue = "true", matchIfMissing = false)
public class AlibabaGraphConfig {

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