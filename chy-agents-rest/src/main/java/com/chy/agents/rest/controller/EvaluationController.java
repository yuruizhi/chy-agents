package com.chy.agents.rest.controller;

import com.chy.agents.core.evaluation.EvaluationFramework;
import com.chy.agents.core.evaluation.EvaluationFramework.EvaluationRequest;
import com.chy.agents.core.evaluation.EvaluationFramework.EvaluationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 模型评估的REST控制器
 */
@RestController
@RequestMapping("/api/v1/evaluation")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationFramework evaluationFramework;

    /**
     * 根据参考评估模型的响应
     *
     * @param request 评估请求
     * @return 评估结果
     */
    @PostMapping
    public EvaluationResult evaluate(@RequestBody EvaluationRequest request) {
        return evaluationFramework.evaluate(request);
    }

    /**
     * 使用自定义指标根据参考评估模型的响应
     *
     * @param request 评估请求
     * @param metrics 要应用的自定义指标
     * @return 评估结果
     */
    @PostMapping("/with-metrics")
    public EvaluationResult evaluateWithMetrics(
            @RequestBody EvaluationRequest request,
            @RequestBody Map<String, Object> metrics) {
        return evaluationFramework.evaluateWithMetrics(request, metrics);
    }
}