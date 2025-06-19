package com.chy.agents.model.alibaba.graph;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.alibaba.cloud.ai.graph.StateGraph;
import com.chy.agents.model.alibaba.graph.config.AlibabaGraphConfig;

/**
 * Alibaba Graph 自动配置类
 * 
 * @author YuRuizhi
 * @date 2025/06/19
 */
@AutoConfiguration
@ConditionalOnClass(StateGraph.class)
@ConditionalOnProperty(prefix = "spring.ai.alibaba.graph", name = "enabled", havingValue = "true", matchIfMissing = false)
@ComponentScan(basePackages = "com.chy.agents.model.alibaba.graph")
@Import(AlibabaGraphConfig.class)
public class AlibabaGraphAutoConfiguration {
    // 自动配置入口
}