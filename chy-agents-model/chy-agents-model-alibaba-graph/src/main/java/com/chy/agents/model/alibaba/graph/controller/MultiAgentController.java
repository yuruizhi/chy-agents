package com.chy.agents.model.alibaba.graph.controller;

import com.alibaba.cloud.ai.graph.StateGraph;
import com.chy.agents.model.alibaba.graph.service.MultiAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 多智能体控制器
 * 
 * @author YuRuizhi
 * @date 2025/06/19
 */
@RestController
@RequestMapping("/api/multi-agent")
@ConditionalOnProperty(prefix = "spring.ai.alibaba.graph", name = "enabled", havingValue = "true")
public class MultiAgentController {

    @Autowired
    private MultiAgentService multiAgentService;
    
    /**
     * 使用研究助手多智能体系统处理请求
     * 
     * @param request 包含用户输入的请求
     * @return 处理结果
     */
    @PostMapping("/research")
    public ResponseEntity<Map<String, String>> research(@RequestBody Map<String, String> request) {
        String input = request.get("input");
        if (input == null || input.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "输入内容不能为空"));
        }
        
        try {
            StateGraph researchAssistant = multiAgentService.createResearchAssistant();
            String result = multiAgentService.executeWorkflow(researchAssistant, input).get();
            return ResponseEntity.ok(Map.of("result", result));
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().body(Map.of("error", "执行失败: " + e.getMessage()));
        }
    }
    
    /**
     * 使用代码评审多智能体系统处理请求
     * 
     * @param request 包含代码和上下文的请求
     * @return 评审结果
     */
    @PostMapping("/code-review")
    public ResponseEntity<Map<String, String>> codeReview(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "代码内容不能为空"));
        }
        
        try {
            StateGraph codeReviewAssistant = multiAgentService.createCodeReviewAssistant();
            String result = multiAgentService.executeWorkflow(codeReviewAssistant, code).get();
            return ResponseEntity.ok(Map.of("result", result));
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().body(Map.of("error", "执行失败: " + e.getMessage()));
        }
    }
    
    /**
     * 异步处理多智能体请求
     * 
     * @param request 包含类型和输入的请求
     * @return 异步响应
     */
    @PostMapping("/async")
    public CompletableFuture<ResponseEntity<Map<String, String>>> asyncProcess(@RequestBody Map<String, String> request) {
        String type = request.getOrDefault("type", "research");
        String input = request.get("input");
        
        if (input == null || input.isEmpty()) {
            return CompletableFuture.completedFuture(
                ResponseEntity.badRequest().body(Map.of("error", "输入内容不能为空"))
            );
        }
        
        StateGraph graph;
        if ("code-review".equals(type)) {
            graph = multiAgentService.createCodeReviewAssistant();
        } else {
            graph = multiAgentService.createResearchAssistant();
        }
        
        return multiAgentService.executeWorkflow(graph, input)
            .thenApply(result -> ResponseEntity.ok(Map.of("result", result)))
            .exceptionally(e -> ResponseEntity.internalServerError()
                .body(Map.of("error", "执行失败: " + e.getMessage())));
    }
}