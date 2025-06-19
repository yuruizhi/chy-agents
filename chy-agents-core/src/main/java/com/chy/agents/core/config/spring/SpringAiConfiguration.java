package com.chy.agents.core.config.spring;

import com.chy.agents.core.agent.Agent;
import com.chy.agents.core.agent.spring.SpringAiAgent;
import com.chy.agents.core.chat.ChatClient;
import com.chy.agents.core.chat.adapter.SpringAiChatClientAdapter;
import com.chy.agents.core.chat.adapter.SpringAiChatClientFactory;
import com.chy.agents.core.router.spring.SpringAiModelRouter;
import com.chy.agents.core.tool.BaseTool;
import com.chy.agents.core.tool.spring.SpringAiToolAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient.MetadataMode;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Spring AI 集成配置类
 * 
 * @author YuRuizhi
 * @date 2025/06/19
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "chy.agents.spring-ai", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SpringAiConfiguration {

    /**
     * 创建Spring AI ChatClient适配器工厂
     */
    @Bean
    @ConditionalOnMissingBean
    public SpringAiChatClientFactory springAiChatClientFactory() {
        return new SpringAiChatClientFactory();
    }
    
    /**
     * 创建ChatClient适配器创建函数
     */
    @Bean
    @ConditionalOnMissingBean
    public Function<org.springframework.ai.chat.client.ChatClient, ChatClient> chatClientAdapterFactory(
            SpringAiChatClientFactory factory) {
        return springAiClient -> {
            String modelId = "unknown";
            try {
                modelId = (String) springAiClient.metadata(MetadataMode.MINIMUM).get("model");
            } catch (Exception e) {
                log.warn("无法获取模型ID: {}", e.getMessage());
            }
            
            String provider = detectProvider(springAiClient);
            return factory.createAdapter(springAiClient, provider, modelId);
        };
    }
    
    /**
     * 创建Spring AI模型路由器
     */
    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "chy.agents.router", name = "type", havingValue = "spring", matchIfMissing = true)
    public SpringAiModelRouter springAiModelRouter(Function<org.springframework.ai.chat.client.ChatClient, ChatClient> adapterFactory) {
        return new SpringAiModelRouter(adapterFactory);
    }
    
    /**
     * 注册默认的Spring AI工具
     */
    @Bean
    public List<Tool> defaultSpringAiTools() {
        return List.of(
                new SpringAiToolAdapter(SpringAiToolAdapter.createSearchTool()),
                new SpringAiToolAdapter(SpringAiToolAdapter.createCalculatorTool()),
                new SpringAiToolAdapter(SpringAiToolAdapter.createDateTimeTool())
        );
    }
    
    /**
     * 创建默认的Agent工具
     */
    @Bean
    public List<Agent.Tool> defaultAgentTools() {
        return Arrays.asList(
                SpringAiToolAdapter.createSearchTool(),
                SpringAiToolAdapter.createCalculatorTool(),
                SpringAiToolAdapter.createDateTimeTool(),
                
                BaseTool.of(
                        "weatherInfo",
                        "获取天气信息",
                        input -> "天气信息: " + input + " 地区晴朗，25°C"
                ),
                
                BaseTool.of(
                        "translateText",
                        "翻译文本",
                        input -> "翻译: " + input + " -> 已翻译的文本"
                )
        );
    }
    
    /**
     * 创建一个示例助手Agent
     */
    @Bean
    @ConditionalOnMissingBean(name = "assistantAgent")
    public Agent assistantAgent(
            @Autowired @Qualifier("openAiChatClient") org.springframework.ai.chat.client.ChatClient openAiClient,
            List<Agent.Tool> defaultAgentTools) {
        
        // 使用Spring AI的ChatClient创建加强的Agent
        SpringAiAgent agent = new SpringAiAgent(
                "assistant-1",
                "智能助手",
                "一个通用的AI助手，可以回答问题、执行任务",
                new com.chy.agents.core.agent.AgentConfig("default", "openai",
                        "你是一个有用的AI助手，可以回答问题并执行各种任务。"),
                openAiClient
        );
        
        // 设置能力和工具
        agent.addCapability("question-answering");
        agent.addCapability("information-retrieval");
        agent.addCapability("task-execution");
        
        agent.setTools(defaultAgentTools);
        agent.start();
        
        return agent;
    }
    
    /**
     * 尝试检测Spring AI客户端的提供商
     */
    private String detectProvider(org.springframework.ai.chat.client.ChatClient client) {
        try {
            String className = client.getClass().getName().toLowerCase();
            if (className.contains("openai")) {
                return "openai";
            } else if (className.contains("alibaba") || className.contains("dashscope")) {
                return "alibaba";
            } else if (className.contains("anthropic") || className.contains("claude")) {
                return "anthropic";
            } else if (className.contains("google") || className.contains("gemini")) {
                return "google";
            } else if (className.contains("azure")) {
                return "azure";
            } else if (className.contains("bedrock")) {
                return "aws";
            } else if (className.contains("ollama")) {
                return "ollama";
            }
        } catch (Exception e) {
            log.warn("检测提供商失败: {}", e.getMessage());
        }
        return "unknown";
    }
    
    /**
     * 增强Spring AI的ChatClient配置
     */
    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "chy.agents.spring-ai", name = "enhance-clients", havingValue = "true", matchIfMissing = true)
    public org.springframework.ai.chat.client.ChatClient enhancedChatClient(
            @Autowired(required = false) @Qualifier("openAiChatClient") org.springframework.ai.chat.client.ChatClient baseClient,
            List<Tool> defaultSpringAiTools) {
        
        if (baseClient == null) {
            log.warn("找不到基础ChatClient，无法创建增强版本");
            return null;
        }
        
        return org.springframework.ai.chat.client.ChatClient.builder(baseClient.getModel())
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()),
                        new SimpleLoggerAdvisor()
                )
                .tools(defaultSpringAiTools)
                .build();
    }
}