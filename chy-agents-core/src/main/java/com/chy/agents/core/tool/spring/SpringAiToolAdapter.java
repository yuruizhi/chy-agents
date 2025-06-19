package com.chy.agents.core.tool.spring;

import com.chy.agents.core.agent.Agent;
import com.chy.agents.core.tool.BaseTool;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.tool.Tool;
import org.springframework.ai.chat.tool.ToolDefinition;
import org.springframework.ai.chat.tool.ToolExecutionRequest;
import org.springframework.ai.chat.tool.ToolParameterDefinition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Spring AI工具适配器
 * 将CHY Agents的工具适配为Spring AI的Tool
 * 
 * @author YuRuizhi
 * @date 2025/06/19
 */
@Slf4j
public class SpringAiToolAdapter implements Tool {

    private final Agent.Tool agentTool;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public SpringAiToolAdapter(Agent.Tool agentTool) {
        this.agentTool = agentTool;
    }
    
    /**
     * 获取工具定义
     */
    @Override
    public ToolDefinition getDefinition() {
        // 为简化示例，我们创建一个基本的参数定义
        ToolParameterDefinition inputParam = ToolParameterDefinition.builder()
                .name("input")
                .description("The input to the tool")
                .type("string")
                .build();
        
        return ToolDefinition.builder()
                .name(agentTool.getName())
                .description(agentTool.getDescription())
                .parameter("input", inputParam)
                .build();
    }
    
    /**
     * 执行工具
     */
    @Override
    public String execute(ToolExecutionRequest toolExecutionRequest) {
        try {
            // 从请求中提取参数
            String input = toolExecutionRequest.getArguments().get("input").toString();
            
            // 执行原始工具
            return agentTool.execute(input);
        } catch (Exception e) {
            log.error("执行工具 [{}] 时发生错误", agentTool.getName(), e);
            return "执行工具时发生错误: " + e.getMessage();
        }
    }
    
    /**
     * 将Spring AI的工具执行请求转换为UserMessage
     */
    public static Message toolExecutionRequestToUserMessage(ToolExecutionRequest request) {
        return new UserMessage("执行工具: " + request.getName() + " 参数: " + request.getArguments());
    }
    
    /**
     * 批量转换工具
     */
    public static List<Tool> convertTools(List<Agent.Tool> agentTools) {
        return agentTools.stream()
                .map(SpringAiToolAdapter::new)
                .collect(Collectors.toList());
    }
    
    /**
     * 创建搜索工具
     */
    public static Agent.Tool createSearchTool() {
        return BaseTool.of(
                "search",
                "搜索互联网上的信息",
                input -> "搜索结果: 关于 '" + input + "' 的信息..."
        );
    }
    
    /**
     * 创建计算工具
     */
    public static Agent.Tool createCalculatorTool() {
        return BaseTool.of(
                "calculator",
                "执行数学计算",
                input -> {
                    try {
                        // 简单示例，实际实现应更复杂
                        return "计算结果: " + Double.valueOf(input);
                    } catch (Exception e) {
                        return "无法计算: " + e.getMessage();
                    }
                }
        );
    }
    
    /**
     * 创建日期时间工具
     */
    public static Agent.Tool createDateTimeTool() {
        return BaseTool.of(
                "datetime",
                "获取当前日期和时间",
                input -> "当前时间: " + java.time.LocalDateTime.now()
        );
    }
}