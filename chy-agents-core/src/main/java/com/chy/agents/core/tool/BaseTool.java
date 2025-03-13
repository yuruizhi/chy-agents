package com.chy.agents.core.tool;

import com.chy.agents.core.agent.Agent;
import lombok.Builder;
import lombok.Data;

/**
 * 基础工具实现
 */
@Data
@Builder
public class BaseTool implements Agent.Tool {
    
    /**
     * 工具名称
     */
    private final String name;
    
    /**
     * 工具描述
     */
    private final String description;
    
    /**
     * 工具执行器
     */
    private final ToolExecutor executor;
    
    @Override
    public String execute(String input) {
        return executor.execute(input);
    }
    
    /**
     * 工具执行器接口
     */
    @FunctionalInterface
    public interface ToolExecutor {
        /**
         * 执行工具
         *
         * @param input 输入参数
         * @return 执行结果
         */
        String execute(String input);
    }
    
    /**
     * 创建简单工具
     *
     * @param name 工具名称
     * @param description 工具描述
     * @param executor 执行器
     * @return 工具实例
     */
    public static BaseTool of(String name, String description, ToolExecutor executor) {
        return BaseTool.builder()
                .name(name)
                .description(description)
                .executor(executor)
                .build();
    }
} 