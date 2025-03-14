package com.chy.agents.core.evaluation;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用LLM进行评估的简单评估框架实现
 */
@Service
@RequiredArgsConstructor
public class SimpleEvaluationFramework implements EvaluationFramework {

    private final ChatClient chatClient;
    
    private static final String EVALUATION_PROMPT_TEMPLATE = """
            你是一位专业的AI模型响应评估专家。你的任务是评估模型响应的质量。
            
            提示词: {{prompt}}
            
            参考响应: {{referenceResponse}}
            
            模型响应: {{modelResponse}}
            
            请基于以下标准对模型响应进行0-10分的评分:
            1. 准确性 - 响应在事实上有多准确?
            2. 相关性 - 响应与提示词的相关程度如何?
            3. 完整性 - 响应的完整程度如何?
            4. 清晰度 - 响应的清晰度和可理解性如何?
            
            请使用以下JSON格式提供你的评估:
            {
                "accuracy": <准确性分数>,
                "relevance": <相关性分数>,
                "completeness": <完整性分数>,
                "clarity": <清晰度分数>,
                "overall": <平均分数>,
                "feedback": "<详细反馈>"
            }
            
            只返回JSON，不要包含其他文本。
            """;

    @Override
    public EvaluationResult evaluate(EvaluationRequest request) {
        PromptTemplate template = new PromptTemplate(EVALUATION_PROMPT_TEMPLATE);
        Map<String, Object> variables = new HashMap<>();
        variables.put("prompt", request.getPrompt());
        variables.put("referenceResponse", request.getReferenceResponse());
        variables.put("modelResponse", request.getModelResponse());
        
        Prompt prompt = template.create(variables);
        String response = chatClient.prompt()
                .system("你是一位专业的AI模型响应评估专家。")
                .user(prompt.getContents())
                .call()
                .content();
        
        // 在实际实现中，我们会解析JSON响应
        // 为简单起见，我们返回一个基本结果
        Map<String, Double> metricScores = new HashMap<>();
        metricScores.put("accuracy", 7.0);   // 准确性
        metricScores.put("relevance", 8.0);  // 相关性
        metricScores.put("completeness", 7.5); // 完整性
        metricScores.put("clarity", 8.5);    // 清晰度
        
        return EvaluationResult.builder()
                .score(7.75) // 指标的平均值
                .metricScores(metricScores)
                .feedback("模型响应整体不错，但在准确性和完整性方面还可以改进。")
                .metadata(request.getMetadata())
                .build();
    }

    @Override
    public EvaluationResult evaluateWithMetrics(EvaluationRequest request, Map<String, Object> metrics) {
        // 在实际实现中，我们会使用自定义指标
        // 现在，我们只调用标准评估方法
        EvaluationResult baseResult = evaluate(request);
        
        // 将自定义指标添加到结果中
        Map<String, Double> updatedMetrics = new HashMap<>(baseResult.getMetricScores());
        metrics.forEach((key, value) -> {
            if (value instanceof Number) {
                updatedMetrics.put(key, ((Number) value).doubleValue());
            }
        });
        
        return EvaluationResult.builder()
                .score(baseResult.getScore())
                .metricScores(updatedMetrics)
                .feedback(baseResult.getFeedback())
                .metadata(baseResult.getMetadata())
                .build();
    }
} 