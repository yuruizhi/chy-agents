package com.chy.agents.rest;

import com.chy.agents.common.config.AiConfig;
import com.chy.agents.common.config.ModelConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * 应用程序入口
 *
 * @author YuRuizhi
 * @date 2025/3/12
 */
@SpringBootApplication
@Import({AiConfig.class, ModelConfig.class})
@ComponentScan(basePackages = {
    "com.chy.agents.rest",
    "com.chy.agents.core",
    "com.chy.agents.chat",
    "com.chy.agents.memory",
    "com.chy.agents.function",
    "com.chy.agents.rag",
    "com.chy.agents.model",
    "com.chy.agents.storage",
    "com.chy.agents.multimodal",
    "com.chy.agents.plugin",
    "com.chy.agents.workflow",
    "com.chy.agents.security"
})
public class ChyAgentsApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ChyAgentsApplication.class, args);
    }
} 