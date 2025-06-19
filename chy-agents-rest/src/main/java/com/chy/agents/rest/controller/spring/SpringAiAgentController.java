package com.chy.agents.rest.controller.spring;

import com.chy.agents.core.agent.Agent;
import com.chy.agents.core.agent.AgentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Spring AI Agent控制器
 * 展示使用Spring AI增强的Agent功能
 * 
 * @author YuRuizhi
 * @date 2025/06/19
 */
@RestController
@RequestMapping("/api/agent/spring")
@ConditionalOnProperty(prefix = "chy.agents.spring-ai", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SpringAiAgentController {

    @Autowired
    @Qualifier("assistantAgent")
    private Agent assistantAgent;
    
    /**
     * 获取代理信息
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getAgentInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("id", assistantAgent.getId());
        info.put("name", assistantAgent.getName());
        info.put("description", assistantAgent.getDescription());
        info.put("status", assistantAgent.getStatus());
        info.put("capabilities", assistantAgent.getCapabilities());
        info.put("tools", assistantAgent.getTools().stream()
                .map(tool -> Map.of(
                        "name", tool.getName(),
                        "description", tool.getDescription()
                ))
                .toList());
        
        return ResponseEntity.ok(info);
    }
    
    /**
     * 使用代理处理请求
     */
    @PostMapping("/process")
    public ResponseEntity<Map<String, String>> process(@RequestBody Map<String, String> request) {
        String input = request.get("input");
        if (input == null || input.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "输入内容不能为空"));
        }
        
        String response = assistantAgent.process(input);
        return ResponseEntity.ok(Map.of("response", response));
    }
    
    /**
     * 异步处理请求
     */
    @PostMapping("/process/async")
    public CompletableFuture<ResponseEntity<Map<String, String>>> processAsync(@RequestBody Map<String, String> request) {
        String input = request.get("input");
        if (input == null || input.isEmpty()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(Map.of("error", "输入内容不能为空"))
            );
        }
        
        Map<String, Object> context = new HashMap<>();
        
        // 可选参数：设置期望能力
        if (request.containsKey("capability")) {
            context.put("requirement", request.get("capability"));
        }
        
        // 可选参数：强制指定提供商
        if (request.containsKey("provider")) {
            context.put("forcedProvider", request.get("provider"));
        }
        
        return assistantAgent.executeAsync(input, context)
                .thenApply(AgentResponse::getContent)
                .thenApply(content -> ResponseEntity.ok(Map.of("response", content)))
                .exceptionally(e -> ResponseEntity.internalServerError()
                        .body(Map.of("error", "处理请求失败: " + e.getMessage())));
    }
    
    /**
     * 控制代理状态
     */
    @PostMapping("/control/{action}")
    public ResponseEntity<Map<String, String>> controlAgent(@PathVariable String action) {
        switch (action.toLowerCase()) {
            case "start" -> {
                assistantAgent.start();
                return ResponseEntity.ok(Map.of("status", "started"));
            }
            case "stop" -> {
                assistantAgent.stop();
                return ResponseEntity.ok(Map.of("status", "stopped"));
            }
            case "reset" -> {
                assistantAgent.reset();
                return ResponseEntity.ok(Map.of("status", "reset"));
            }
            default -> {
                return ResponseEntity.badRequest().body(Map.of("error", "不支持的操作: " + action));
            }
        }
    }
}