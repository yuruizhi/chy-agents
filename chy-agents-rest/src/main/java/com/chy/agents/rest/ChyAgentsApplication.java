package com.chy.agents.rest;

import com.chy.agents.common.config.AiConfig;
import com.chy.agents.common.config.ModelConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * 应用程序入口
 *
 * @author YuRuizhi
 * @date 2025/3/12
 */
@SpringBootApplication
@Import({AiConfig.class, ModelConfig.class})
public class ChyAgentsApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ChyAgentsApplication.class, args);
    }
} 