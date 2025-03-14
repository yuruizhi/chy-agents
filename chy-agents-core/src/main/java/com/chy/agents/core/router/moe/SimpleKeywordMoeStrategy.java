package com.chy.agents.core.router.moe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 简单关键词匹配的MoE路由策略实现
 * 基于关键词匹配度选择最合适的专家模型
 */
@Service
public class SimpleKeywordMoeStrategy implements MoeRoutingStrategy {

    private static final Logger logger = LoggerFactory.getLogger(SimpleKeywordMoeStrategy.class);
    
    // 默认提供商（当没有明显匹配时使用）
    private String defaultProvider = "openai";
    
    // 提供商优先级映射
    private final Map<String, Integer> providerPriorities = new HashMap<>();
    
    /**
     * 构造函数，初始化默认优先级
     */
    public SimpleKeywordMoeStrategy() {
        // 设置默认的提供商优先级
        providerPriorities.put("anthropic", 100);  // Claude模型优先级最高
        providerPriorities.put("openai", 90);      // OpenAI模型次之
        providerPriorities.put("google", 80);      // Google模型再次
        providerPriorities.put("deepseek", 70);    // DeepSeek模型
        providerPriorities.put("mistral", 60);     // Mistral模型
    }
    
    /**
     * 根据输入内容和专家领域配置，选择最合适的专家模型
     *
     * @param input 用户输入内容
     * @param expertiseMap 专家领域配置映射（key: 提供商名称，value: 专长领域列表）
     * @return 最合适的提供商名称
     */
    @Override
    public String selectBestExpert(String input, Map<String, List<String>> expertiseMap) {
        if (input == null || input.trim().isEmpty()) {
            logger.warn("输入内容为空，使用默认提供商：{}", defaultProvider);
            return defaultProvider;
        }
        
        // 如果专长映射为空，使用默认提供商
        if (expertiseMap == null || expertiseMap.isEmpty()) {
            return defaultProvider;
        }
        
        // 计算每个提供商的匹配得分
        Map<String, Double> scores = new HashMap<>();
        
        for (Map.Entry<String, List<String>> entry : expertiseMap.entrySet()) {
            String provider = entry.getKey();
            List<String> expertises = entry.getValue();
            
            // 计算该提供商所有专长领域的最大匹配度
            double maxScore = 0.0;
            for (String expertise : expertises) {
                double score = calculateMatchScore(input, expertise);
                maxScore = Math.max(maxScore, score);
            }
            
            // 考虑优先级因素进行权重调整
            double priorityFactor = getPriorityFactor(provider);
            double finalScore = maxScore * priorityFactor;
            
            scores.put(provider, finalScore);
            logger.debug("提供商 [{}] 的匹配分数: {} (原始分数: {}, 优先级因子: {})", 
                    provider, finalScore, maxScore, priorityFactor);
        }
        
        // 找出得分最高的提供商
        return scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(defaultProvider);
    }
    
    /**
     * 计算特定输入内容与特定领域的匹配度
     *
     * @param input 用户输入内容
     * @param domain 专长领域
     * @return 匹配度评分（0-1之间，越高表示匹配度越高）
     */
    @Override
    public double calculateMatchScore(String input, String domain) {
        if (input == null || domain == null) {
            return 0.0;
        }
        
        // 简化输入和领域字符串
        String normalizedInput = normalizeText(input);
        String normalizedDomain = normalizeText(domain);
        
        // 计算关键词匹配率
        Set<String> inputWords = tokenize(normalizedInput);
        Set<String> domainWords = tokenize(normalizedDomain);
        
        // 计算交集大小
        Set<String> intersection = new HashSet<>(inputWords);
        intersection.retainAll(domainWords);
        
        // 计算Jaccard相似度
        double jaccardSimilarity = 0.0;
        if (!inputWords.isEmpty() || !domainWords.isEmpty()) {
            Set<String> union = new HashSet<>(inputWords);
            union.addAll(domainWords);
            jaccardSimilarity = (double) intersection.size() / union.size();
        }
        
        // 检查是否包含完整短语
        double phraseBonus = 0.0;
        if (normalizedInput.contains(normalizedDomain)) {
            phraseBonus = 0.3; // 包含完整领域短语给予额外加分
        }
        
        return Math.min(1.0, jaccardSimilarity + phraseBonus);
    }
    
    /**
     * 获取提供商之间的权重分配
     *
     * @param input 用户输入内容
     * @param providers 提供商列表
     * @return 权重分配映射（key: 提供商名称，value: 权重值）
     */
    @Override
    public Map<String, Double> getProviderWeights(String input, List<String> providers) {
        if (providers == null || providers.isEmpty()) {
            return Collections.emptyMap();
        }
        
        Map<String, Double> weights = new HashMap<>();
        double totalPriority = 0.0;
        
        // 根据优先级计算初始权重
        for (String provider : providers) {
            double priority = providerPriorities.getOrDefault(provider, 0);
            weights.put(provider, priority);
            totalPriority += priority;
        }
        
        // 归一化权重
        if (totalPriority > 0) {
            for (Map.Entry<String, Double> entry : weights.entrySet()) {
                entry.setValue(entry.getValue() / totalPriority);
            }
        } else {
            // 如果所有优先级都为0，平均分配
            double equalWeight = 1.0 / providers.size();
            for (String provider : providers) {
                weights.put(provider, equalWeight);
            }
        }
        
        return weights;
    }
    
    /**
     * 设置默认提供商
     *
     * @param defaultProvider 默认提供商名称
     */
    public void setDefaultProvider(String defaultProvider) {
        this.defaultProvider = defaultProvider;
    }
    
    /**
     * 设置提供商优先级
     *
     * @param provider 提供商名称
     * @param priority 优先级值
     */
    public void setProviderPriority(String provider, int priority) {
        providerPriorities.put(provider, priority);
    }
    
    /**
     * 根据提供商优先级计算权重因子
     *
     * @param provider 提供商名称
     * @return 权重因子（1.0 + 归一化的优先级加成）
     */
    private double getPriorityFactor(String provider) {
        int priority = providerPriorities.getOrDefault(provider, 0);
        int maxPriority = providerPriorities.values().stream()
                .max(Integer::compare)
                .orElse(100);
        
        // 优先级因子：1.0 + 归一化的优先级加成（最高0.5）
        return 1.0 + (0.5 * priority / maxPriority);
    }
    
    /**
     * 规范化文本（小写化、去除特殊字符）
     *
     * @param text 输入文本
     * @return 规范化后的文本
     */
    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }
        return text.toLowerCase()
                .replaceAll("[\\p{Punct}&&[^\\p{IsAlphabetic}]]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
    
    /**
     * 将文本分割为词元集合
     *
     * @param text 输入文本
     * @return 词元集合
     */
    private Set<String> tokenize(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptySet();
        }
        
        // 简单的基于空格的分词
        return Arrays.stream(text.split("\\s+"))
                .filter(word -> word.length() > 1) // 过滤掉单字符词
                .collect(Collectors.toSet());
    }
} 