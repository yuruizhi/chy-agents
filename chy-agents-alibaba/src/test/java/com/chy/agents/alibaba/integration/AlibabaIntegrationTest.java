package com.chy.agents.alibaba.integration;

import com.chy.agents.alibaba.client.AlibabaChatClient;
import com.chy.agents.alibaba.config.AlibabaAutoConfiguration;
import com.chy.agents.alibaba.config.AlibabaConfig;
import com.chy.agents.core.chat.ChatClient;
import com.chy.agents.core.chat.message.Message;
import com.chy.agents.core.chat.prompt.Prompt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = {AlibabaAutoConfiguration.class, AlibabaConfig.class})
@ActiveProfiles("test")
class AlibabaIntegrationTest {

    @Autowired
    private ChatClient chatClient;

    @Test
    void shouldLoadConfiguration() {
        assertThat(chatClient).isNotNull()
            .isInstanceOf(AlibabaChatClient.class);
    }

    @Test
    void shouldHaveCorrectConfiguration() {
        assertThat(chatClient.getProvider()).isEqualTo("alibaba");
        assertThat(chatClient.getModel()).isEqualTo("qwen-max");
        assertThat(chatClient.getConfig())
            .containsEntry("provider", "alibaba")
            .containsEntry("model", "qwen-max")
            .containsEntry("endpoint", "https://test-endpoint");
    }

    @Test
    void shouldHandleAsyncCall() throws Exception {
        Prompt prompt = Prompt.of("Test prompt");
        CompletableFuture<Message> future = chatClient.callAsync(prompt);
        
        Message response = future.get(5, TimeUnit.SECONDS);
        assertThat(response).isNotNull();
        assertThat(response.getRole()).isEqualTo(Message.Role.ASSISTANT);
    }

    @Test
    void shouldHandleStreamingCall() {
        Prompt prompt = Prompt.of("Test prompt");
        List<Message> messages = chatClient.stream(prompt);
        
        assertThat(messages).isNotEmpty();
        assertThat(messages.get(0).getRole()).isEqualTo(Message.Role.ASSISTANT);
    }

    @Test
    void shouldHandleSystemPrompt() {
        Prompt prompt = Prompt.builder()
            .systemPrompt("System instruction")
            .userInput("User input")
            .build();
            
        Message response = chatClient.call(prompt);
        
        assertThat(response).isNotNull();
        assertThat(response.getRole()).isEqualTo(Message.Role.ASSISTANT);
    }
} 