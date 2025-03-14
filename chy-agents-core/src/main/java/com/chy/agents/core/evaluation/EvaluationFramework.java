package com.chy.agents.core.evaluation;

import lombok.Data;
import lombok.Builder;
import java.util.Map;

/**
 * 模型评估框架接口
 */
public interface EvaluationFramework {
    
    /**
     * 根据参考评估模型的响应
     * 
     * @param request 评估请求
     * @return 评估结果
     */
    EvaluationResult evaluate(EvaluationRequest request);
    
    /**
     * 使用自定义指标根据参考评估模型的响应
     * 
     * @param request 评估请求
     * @param metrics 要应用的自定义指标
     * @return 评估结果
     */
    EvaluationResult evaluateWithMetrics(EvaluationRequest request, Map<String, Object> metrics);
    
    /**
     * 模型评估请求
     */
    @Data
    @Builder
    class EvaluationRequest {
        private String modelResponse;    // 模型响应
        private String referenceResponse; // 参考响应
        private String prompt;            // 提示词
        private Map<String, Object> metadata; // 元数据
    }
    
    /**
     * 模型评估结果
     */
    @Data
    @Builder
    class EvaluationResult {
        private double score;            // 总体评分
        private Map<String, Double> metricScores; // 各项指标得分
        private String feedback;         // 反馈意见
        private Map<String, Object> metadata; // 元数据
    }
}