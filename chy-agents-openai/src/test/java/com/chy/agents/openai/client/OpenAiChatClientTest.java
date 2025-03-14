package com.chy.agents.openai.client;

import com.chy.agents.core.chat.message.Message;
import com.chy.agents.core.chat.prompt.Prompt;
import com.chy.agents.openai.config.OpenAiConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.openai.api.OpenAiApi;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenAiChatClientTest {

    @Mock
    private OpenAiApi openAiApi;

    @Mock
    private ChatResponse chatResponse;

    @Mock
    private Generation generation;

    private OpenAiConfig config;
    private OpenAiChatClient chatClient;

    @BeforeEach
    void setUp() {
        config = new OpenAiConfig();
        config.setApiKey("test-api-key");
        config.setModel("gpt-4");
        config.setTemperature(0.7f);
        config.setMaxTokens(4096);
        config.setEndpoint("https://api.openai.com");
        config.setTimeout(30);

        chatClient = new OpenAiChatClient(openAiApi, config);
    }

    @Test
    void testGetConfig() {
        Map<String, Object> configMap = chatClient.getConfig();
        
        assertThat(configMap).containsEntry("provider", "openai");
        assertThat(configMap).containsEntry("model", "gpt-4");
        assertThat(configMap).containsEntry("temperature", 0.7f);
        assertThat(configMap).containsEntry("maxTokens", 4096);
    }

    @Test
    void testGetProvider() {
        assertThat(chatClient.getProvider()).isEqualTo("openai");
    }

    @Test
    void testGetModel() {
        assertThat(chatClient.getModel()).isEqualTo("gpt-4");
    }

    @Test
    void testCall() {
        // Setup mock response
        when(generation.getOutput()).thenReturn(new org.springframework.ai.chat.messages.AssistantMessage("Test response"));
        when(chatResponse.getResult()).thenReturn(generation);
        when(openAiApi.chatCompletion(any(), any())).thenReturn(chatResponse);

        // Create prompt
        Prompt prompt = Prompt.builder()
                .systemPrompt("You are a helpful assistant.")
                .userInput("Hello, how are you?")
                .build();

        // Call the client
        Message response = chatClient.call(prompt);

        // Verify response
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo("Test response");
        assertThat(response.getRole()).isEqualTo(Message.Role.ASSISTANT);
    }

    @Test
    void testCallAsync() throws Exception {
        // Setup mock response
        when(generation.getOutput()).thenReturn(new org.springframework.ai.chat.messages.AssistantMessage("Test response"));
        when(chatResponse.getResult()).thenReturn(generation);
        when(openAiApi.chatCompletion(any(), any())).thenReturn(chatResponse);

        // Create prompt
        Prompt prompt = Prompt.builder()
                .systemPrompt("You are a helpful assistant.")
                .userInput("Hello, how are you?")
                .build();

        // Call the client asynchronously
        Message response = chatClient.callAsync(prompt).get();

        // Verify response
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo("Test response");
        assertThat(response.getRole()).isEqualTo(Message.Role.ASSISTANT);
    }
} 