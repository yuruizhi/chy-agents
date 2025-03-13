package com.chy.agents.alibaba.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AlibabaConfig.class)
@EnableConfigurationProperties(AlibabaConfig.class)
@TestPropertySource(properties = {
    "chy.agents.alibaba.api-key=test-api-key",
    "chy.agents.alibaba.model=qwen-max",
    "chy.agents.alibaba.endpoint=https://test-endpoint",
    "chy.agents.alibaba.temperature=0.8",
    "chy.agents.alibaba.max-tokens=2048",
    "chy.agents.alibaba.timeout=60",
    "chy.agents.alibaba.access-key-id=test-access-key",
    "chy.agents.alibaba.access-key-secret=test-access-secret",
    "chy.agents.alibaba.region=cn-hangzhou",
    "chy.agents.alibaba.proxy=http://proxy:8080"
})
class AlibabaConfigTest {

    @Autowired
    private AlibabaConfig config;

    @Test
    void shouldLoadConfigurationProperties() {
        assertThat(config.getApiKey()).isEqualTo("test-api-key");
        assertThat(config.getModel()).isEqualTo("qwen-max");
        assertThat(config.getEndpoint()).isEqualTo("https://test-endpoint");
        assertThat(config.getTemperature()).isEqualTo(0.8f);
        assertThat(config.getMaxTokens()).isEqualTo(2048);
        assertThat(config.getTimeout()).isEqualTo(60);
        assertThat(config.getAccessKeyId()).isEqualTo("test-access-key");
        assertThat(config.getAccessKeySecret()).isEqualTo("test-access-secret");
        assertThat(config.getRegion()).isEqualTo("cn-hangzhou");
        assertThat(config.getProxy()).isEqualTo("http://proxy:8080");
    }

    @Test
    void shouldHaveDefaultValues() {
        AlibabaConfig defaultConfig = new AlibabaConfig();
        
        assertThat(defaultConfig.getModel()).isEqualTo("qwen-max");
        assertThat(defaultConfig.getEndpoint())
            .isEqualTo("https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation");
        assertThat(defaultConfig.getTemperature()).isEqualTo(0.7f);
        assertThat(defaultConfig.getMaxTokens()).isEqualTo(4096);
        assertThat(defaultConfig.getTimeout()).isEqualTo(30);
        assertThat(defaultConfig.getRegion()).isEqualTo("cn-hangzhou");
        assertThat(defaultConfig.getMaxRetries()).isEqualTo(3);
    }

    @Test
    void shouldValidateConfiguration() {
        assertThat(config.getTemperature()).isBetween(0.0f, 1.0f);
        assertThat(config.getMaxTokens()).isPositive();
        assertThat(config.getTimeout()).isPositive();
        assertThat(config.getMaxRetries()).isNotNegative();
    }
} 