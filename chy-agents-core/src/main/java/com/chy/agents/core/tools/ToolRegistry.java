package com.chy.agents.core.tools;

import com.chy.agents.core.agent.Agent.Tool;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ToolRegistry {
    
    private final Map<String, Tool> tools = new HashMap<>();
    
    public void registerTool(Tool tool) {
        tools.put(tool.getName(), tool);
    }
    
    public Tool getTool(String name) {
        return tools.get(name);
    }
    
    public List<Tool> getAllTools() {
        return tools.values().stream().collect(Collectors.toList());
    }
    
    public List<Tool> getToolsByCategory(String category) {
        return tools.values().stream()
            .filter(tool -> tool.getDescription().contains(category))
            .collect(Collectors.toList());
    }
} 