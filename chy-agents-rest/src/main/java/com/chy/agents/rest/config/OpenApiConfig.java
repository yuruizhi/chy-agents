package com.chy.agents.rest.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI配置
 *
 * @author YuRuizhi
 * @date 2025/3/12
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .components(new Components())
            .info(new Info()
                .title("CHY Agents API")
                .description("基于Spring AI构建的智能代理系统")
                .version("v0.0.1")
                .license(new License().name("Apache 2.0")));
    }
} 