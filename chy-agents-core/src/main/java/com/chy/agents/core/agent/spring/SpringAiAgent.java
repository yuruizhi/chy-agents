package com.chy.agents.core.agent.spring;

import com.chy.agents.core.agent.Agent;
import com.chy.agents.core.agent.AgentConfig;
import com.chy.agents.core.agent.AgentResponse;
import com.chy.agents.core.agent.AgentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.client.advisor.Advisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.generation.StreamingChatGenerator;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 基于Spring AI的智能代理实现
 * 
 * @author YuRuizhi
 * @date 2025/06/19
 */
@Slf4j
public class SpringAiAgent implements Agent {
    
    private final String id;
    private final String name;
    private final String description;
    private final AgentConfig config;
    private AgentStatus status = AgentStatus.INITIALIZED;
    private final List<String> capabilities = new ArrayList<>();
    private List<Tool> tools = new ArrayList<>();
    private Memory memory;
    private final ChatClient chatClient;
    private final Map<String, Object> context = new ConcurrentHashMap<>();
    private final ChatMemory chatMemory;
    
    public SpringAiAgent(String id, String name, String description, AgentConfig config, ChatClient chatClient) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.config = config;
        this.chatClient = chatClient;
        this.memory = new SpringChatMemory();
        this.chatMemory = new InMemoryChatMemory();
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public AgentConfig getConfig() {
        return config;
    }
    
    @Override
    public String process(String input) {
        return process(input, Collections.emptyMap());
    }
    
    @Override
    public String process(String input, Map<String, Object> contextParams) {
        if (status != AgentStatus.RUNNING) {
            log.warn("代理 [{}] 未处于运行状态，当前状态: {}", id, status);
            return "Agent is not running. Current status: " + status;
        }
        
        try {
            AgentResponse response = execute(input, contextParams);
            return response.getContent();
        } catch (Exception e) {
            log.error("代理 [{}] 处理请求失败", id, e);
            status = AgentStatus.ERROR;
            return "处理请求时发生错误: " + e.getMessage();
        }
    }
    
    @Override
    public AgentResponse execute(String input, Map<String, Object> contextParams) {
        try {
            // 更新上下文
            if (contextParams != null) {
                context.putAll(contextParams);
            }
            
            log.info("代理 [{}] 收到执行请求: {}", id, input);
            
            // 准备消息列表
            List<Message> messages = new ArrayList<>();
            
            // 添加系统提示词
            if (config.getSystemPrompt() != null && !config.getSystemPrompt().isEmpty()) {
                messages.add(new SystemMessage(config.getSystemPrompt()));
            }
            
            // 添加记忆中的消息
            if (memory != null) {
                List<com.chy.agents.core.chat.message.Message> memoryMessages = memory.getAll();
                for (com.chy.agents.core.chat.message.Message msg : memoryMessages) {
                    messages.add(convertToSpringMessage(msg));
                }
            }
            
            // 添加当前用户输入
            messages.add(new UserMessage(input));
            
            // 构建提示词
            Prompt prompt = new Prompt(messages);
            
            // 调用模型
            org.springframework.ai.chat.ChatResponse response = chatClient.prompt(prompt)
                    .call();
            
            // 保存到记忆
            if (memory != null) {
                memory.add(new com.chy.agents.core.chat.message.BaseMessage(
                        com.chy.agents.core.chat.message.Message.Role.USER, 
                        input));
                memory.add(new com.chy.agents.core.chat.message.BaseMessage(
                        com.chy.agents.core.chat.message.Message.Role.ASSISTANT, 
                        response.getResult().getOutput().getContent()));
            }
            
            String content = response.getResult().getOutput().getContent();
            log.info("代理 [{}] 生成响应: {}", id, content);
            return AgentResponse.ofText(content);
            
        } catch (Exception e) {
            log.error("代理 [{}] 执行请求失败", id, e);
            status = AgentStatus.ERROR;
            return AgentResponse.ofError(e.getMessage());
        }
    }
    
    @Override
    public CompletableFuture<AgentResponse> executeAsync(String input, Map<String, Object> contextParams) {
        return CompletableFuture.supplyAsync(() -> execute(input, contextParams));
    }
    
    @Override
    public List<String> getCapabilities() {
        return Collections.unmodifiableList(capabilities);
    }
    
    @Override
    public boolean hasCapability(String capability) {
        return capabilities.contains(capability);
    }
    
    public void addCapability(String capability) {
        capabilities.add(capability);
        log.info("代理 [{}] 添加能力: {}", id, capability);
    }
    
    public void removeCapability(String capability) {
        capabilities.remove(capability);
        log.info("代理 [{}] 移除能力: {}", id, capability);
    }
    
    @Override
    public AgentStatus getStatus() {
        return status;
    }
    
    @Override
    public void start() {
        if (status == AgentStatus.RUNNING) {
            log.info("代理 [{}] 已经在运行中", id);
            return;
        }
        
        log.info("启动代理 [{}]", id);
        status = AgentStatus.RUNNING;
    }
    
    @Override
    public void stop() {
        if (status == AgentStatus.STOPPED) {
            log.info("代理 [{}] 已经停止", id);
            return;
        }
        
        log.info("停止代理 [{}]", id);
        status = AgentStatus.STOPPED;
    }
    
    @Override
    public void reset() {
        log.info("重置代理 [{}]", id);
        context.clear();
        if (memory != null) {
            memory.clear();
        }
        status = AgentStatus.INITIALIZED;
        
        // 重置Spring AI的聊天记忆
        if (chatMemory != null) {
            chatMemory.clear();
        }
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
    
    /**
     * 将项目内部消息转换为Spring AI消息
     */
    private Message convertToSpringMessage(com.chy.agents.core.chat.message.Message message) {
        return switch (message.getRole()) {
            case USER -> new UserMessage(message.getContent());
            case ASSISTANT -> new org.springframework.ai.chat.messages.AssistantMessage(message.getContent());
            case SYSTEM -> new SystemMessage(message.getContent());
            default -> new UserMessage(message.getContent());
        };
    }
    
    /**
     * Spring AI聊天记忆适配器
     */
    private class SpringChatMemory implements Memory {
        @Override
        public void add(com.chy.agents.core.chat.message.Message message) {
            Message springMessage = convertToSpringMessage(message);
            chatMemory.add(springMessage);
        }
        
        @Override
        public List<com.chy.agents.core.chat.message.Message> get(int limit) {
            return chatMemory.getMessages().stream()
                    .limit(limit)
                    .map(this::convertFromSpringMessage)
                    .collect(Collectors.toList());
        }
        
        @Override
        public void clear() {
            chatMemory.clear();
        }
        
        @Override
        public List<com.chy.agents.core.chat.message.Message> getAll() {
            return chatMemory.getMessages().stream()
                    .map(this::convertFromSpringMessage)
                    .collect(Collectors.toList());
        }
        
        @Override
        public int size() {
            return chatMemory.getMessages().size();
        }
        
        /**
         * 将Spring AI消息转换为项目内部消息
         */
        private com.chy.agents.core.chat.message.Message convertFromSpringMessage(Message message) {
            com.chy.agents.core.chat.message.Message.Role role;
            
            if (message instanceof UserMessage) {
                role = com.chy.agents.core.chat.message.Message.Role.USER;
            } else if (message instanceof org.springframework.ai.chat.messages.AssistantMessage) {
                role = com.chy.agents.core.chat.message.Message.Role.ASSISTANT;
            } else if (message instanceof SystemMessage) {
                role = com.chy.agents.core.chat.message.Message.Role.SYSTEM;
            } else {
                role = com.chy.agents.core.chat.message.Message.Role.USER;
            }
            
            return new com.chy.agents.core.chat.message.BaseMessage(role, message.getContent());
        }
    }
}