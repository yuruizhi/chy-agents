package com.chy.agents.alibaba.config;

import com.chy.agents.core.chat.ChatClient;
import com.chy.agents.alibaba.client.AlibabaChatClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class AlibabaAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(AlibabaAutoConfiguration.class))
        .withUserConfiguration(AlibabaConfig.class)
        .withPropertyValues(
            "chy.agents.alibaba.api-key=test-api-key",
            "chy.agents.alibaba.model=qwen-max",
            "chy.agents.alibaba.endpoint=https://test-endpoint"
        );

    @Test
    void shouldCreateChatClientWhenApiKeyIsPresent() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ChatClient.class);
            assertThat(context).hasSingleBean(AlibabaChatClient.class);
            
            ChatClient chatClient = context.getBean(ChatClient.class);
            assertThat(chatClient.getProvider()).isEqualTo("alibaba");
            assertThat(chatClient.getModel()).isEqualTo("qwen-max");
        });
    }

    @Test
    void shouldNotCreateChatClientWhenApiKeyIsMissing() {
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AlibabaAutoConfiguration.class))
            .withUserConfiguration(AlibabaConfig.class)
            .run(context -> {
                assertThat(context).doesNotHaveBean(ChatClient.class);
                assertThat(context).doesNotHaveBean(AlibabaChatClient.class);
            });
    }

    @Test
    void shouldCreateChatClientWithCustomConfiguration() {
        contextRunner
            .withPropertyValues(
                "chy.agents.alibaba.temperature=0.8",
                "chy.agents.alibaba.max-tokens=2048",
                "chy.agents.alibaba.timeout=60"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(ChatClient.class);
                
                AlibabaConfig config = context.getBean(AlibabaConfig.class);
                assertThat(config.getTemperature()).isEqualTo(0.8f);
                assertThat(config.getMaxTokens()).isEqualTo(2048);
                assertThat(config.getTimeout()).isEqualTo(60);
            });
    }

    @Test
    void shouldCreateChatClientWithAccessKeyConfiguration() {
        contextRunner
            .withPropertyValues(
                "chy.agents.alibaba.access-key-id=test-access-key",
                "chy.agents.alibaba.access-key-secret=test-secret-key"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(ChatClient.class);
                
                AlibabaConfig config = context.getBean(AlibabaConfig.class);
                assertThat(config.getAccessKeyId()).isEqualTo("test-access-key");
                assertThat(config.getAccessKeySecret()).isEqualTo("test-secret-key");
            });
    }
} 