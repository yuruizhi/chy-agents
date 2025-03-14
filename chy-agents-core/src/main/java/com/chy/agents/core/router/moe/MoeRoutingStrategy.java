package com.chy.agents.core.router.moe;

import java.util.List;
import java.util.Map;

/**
 * MoE（Mixture of Experts）路由策略接口
 * 定义基于专家混合的路由策略，根据输入内容选择最合适的"专家"模型
 */
public interface MoeRoutingStrategy {
    
    /**
     * 根据输入内容和专家领域配置，选择最合适的专家模型
     *
     * @param input 用户输入内容
     * @param expertiseMap 专家领域配置映射（key: 提供商名称，value: 专长领域列表）
     * @return 最合适的提供商名称
     */
    String selectBestExpert(String input, Map<String, List<String>> expertiseMap);
    
    /**
     * 计算特定输入内容与特定领域的匹配度
     *
     * @param input 用户输入内容
     * @param domain 专长领域
     * @return 匹配度评分（0-1之间，越高表示匹配度越高）
     */
    double calculateMatchScore(String input, String domain);
    
    /**
     * 获取提供商之间的权重分配
     *
     * @param input 用户输入内容
     * @param providers 提供商列表
     * @return 权重分配映射（key: 提供商名称，value: 权重值）
     */
    Map<String, Double> getProviderWeights(String input, List<String> providers);
} 