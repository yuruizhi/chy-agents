package com.chy.agents.core.agent;

import com.chy.agents.core.agent.Agent.Tool;
import com.chy.agents.core.agent.Agent.Memory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 简单代理实现
 *
 * @author YuRuizhi
 * @date 2025/3/12
 */
@Slf4j
public class SimpleAgent implements Agent {
    
    private String name;
    private String description;
    private List<Tool> tools;
    private Memory memory;
    private final ChatClient chatClient;
    
    public SimpleAgent(String name, String description, ChatClient chatClient) {
        this.name = name;
        this.description = description;
        this.tools = new ArrayList<>();
        this.memory = new SimpleMemory();
        this.chatClient = chatClient;
    }
    
    @Override
    public String execute(String input, String modelProvider) {
        ChatClient client = modelRouter.selectClient(modelProvider);
        // 使用指定provider的client执行
        
        // 准备所有消息，包括记忆和当前输入
        // 获取最近10条消息
        List<Message> messages = new ArrayList<>(memory.get(10));
        
        // 添加系统提示
        String systemPrompt = generateSystemPrompt();
        messages.add(new SystemMessage(systemPrompt));
        
        // 添加用户输入
        messages.add(new UserMessage(input));
        
        // 调用LLM
        Prompt prompt = new Prompt(messages);
        String response = client.call(prompt).getResult().getOutput().getContent();
        
        // 解析响应中的工具调用
        if (containsToolCall(response)) {
            response = executeToolCalls(response);
        }
        
        // 记录对话
        memory.add(new UserMessage(input));
        memory.add(new SimpleAssistantMessage(response));
        
        return response;
    }
    
    private String generateSystemPrompt() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("description", description);
        
        if (tools != null && !tools.isEmpty()) {
            StringBuilder toolsInfo = new StringBuilder();
            toolsInfo.append("可用工具:\n");
            for (Tool tool : tools) {
                toolsInfo.append("- ").append(tool.getName()).append(": ")
                         .append(tool.getDescription()).append("\n");
            }
            variables.put("tools", toolsInfo.toString());
        } else {
            variables.put("tools", "无可用工具");
        }
        
        SystemPromptTemplate systemTemplate = new SystemPromptTemplate("""
            你是{name}，{description}
            {tools}
            如需使用工具，请使用以下格式：
            
            ```tool
            工具名称: 工具参数
            ```
            
            请回复用户的问题，必要时可以调用工具。
            """);
        
        return systemTemplate.render(variables);
    }
    
    private boolean containsToolCall(String response) {
        return response.contains("```tool") && response.contains("```");
    }
    
    private String executeToolCalls(String response) {
        Pattern pattern = Pattern.compile("```tool\\s*([\\s\\S]*?)\\s*```");
        Matcher matcher = pattern.matcher(response);
        
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String toolCall = matcher.group(1);
            String[] parts = toolCall.split(":", 2);
            if (parts.length == 2) {
                String toolName = parts[0].trim();
                String toolInput = parts[1].trim();
                
                Tool tool = findToolByName(toolName);
                if (tool != null) {
                    String toolOutput = tool.execute(toolInput);
                    matcher.appendReplacement(result, "我使用工具 " + toolName + " 获得结果：\n\n" + toolOutput);
                } else {
                    matcher.appendReplacement(result, "抱歉，找不到工具：" + toolName);
                }
            }
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
    
    private Tool findToolByName(String name) {
        if (tools == null) return null;
        
        for (Tool tool : tools) {
            if (tool.getName().equalsIgnoreCase(name)) {
                return tool;
            }
        }
        return null;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public List<Tool> getTools() {
        return tools;
    }
    
    @Override
    public void setTools(List<Tool> tools) {
        this.tools = tools;
    }
    
    @Override
    public Memory getMemory() {
        return memory;
    }
    
    @Override
    public void setMemory(Memory memory) {
        this.memory = memory;
    }

} 