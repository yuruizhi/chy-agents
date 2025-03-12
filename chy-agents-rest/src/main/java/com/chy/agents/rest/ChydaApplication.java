package com.chy.agents.rest;

import com.chy.agents.common.config.AiConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(AiConfig.class)
public class ChydaApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ChydaApplication.class, args);
    }
} 