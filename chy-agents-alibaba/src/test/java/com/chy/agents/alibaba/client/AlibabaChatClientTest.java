package com.chy.agents.alibaba.client;

import com.chy.agents.alibaba.config.AlibabaConfig;
import com.chy.agents.core.chat.message.Message;
import com.chy.agents.core.chat.prompt.Prompt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlibabaChatClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<HttpEntity<Map<String, Object>>> requestCaptor;

    private AlibabaConfig config;
    private AlibabaChatClient chatClient;

    @BeforeEach
    void setUp() {
        config = new AlibabaConfig();
        config.setApiKey("test-api-key");
        config.setModel("qwen-max");
        config.setEndpoint("https://test-endpoint");
        config.setTemperature(0.7f);
        config.setMaxTokens(4096);
        config.setAccessKeyId("test-access-key");
        config.setAccessKeySecret("test-secret-key");

        chatClient = new AlibabaChatClient(config);
        // 使用反射注入mock的RestTemplate
        try {
            java.lang.reflect.Field field = AlibabaChatClient.class.getDeclaredField("restTemplate");
            field.setAccessible(true);
            field.set(chatClient, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldCallModelSuccessfully() {
        // Given
        Prompt prompt = Prompt.of("Test prompt");
        Map<String, Object> response = Map.of(
            "output", Map.of(
                "text", "Test response"
            )
        );

        when(restTemplate.postForObject(
            eq(config.getEndpoint()),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(response);

        // When
        Message result = chatClient.call(prompt);

        // Then
        verify(restTemplate).postForObject(
            eq(config.getEndpoint()),
            requestCaptor.capture(),
            eq(Map.class)
        );

        HttpEntity<Map<String, Object>> request = requestCaptor.getValue();
        assertThat(request.getHeaders().get("Authorization")).contains("Bearer " + config.getApiKey());
        assertThat(request.getHeaders().get("Content-Type")).contains("application/json");
        assertThat(request.getBody())
            .containsEntry("model", config.getModel())
            .containsKey("messages");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Test response");
        assertThat(result.getRole()).isEqualTo(Message.Role.ASSISTANT);
    }

    @Test
    void shouldHandleErrorResponse() {
        // Given
        Prompt prompt = Prompt.of("Test prompt");

        when(restTemplate.postForObject(
            eq(config.getEndpoint()),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(null);

        // When/Then
        assertThatThrownBy(() -> chatClient.call(prompt))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Invalid response from Alibaba model");
    }

    @Test
    void shouldCallAsyncSuccessfully() throws Exception {
        // Given
        Prompt prompt = Prompt.of("Test prompt");
        Map<String, Object> response = Map.of(
            "output", Map.of(
                "text", "Test response"
            )
        );

        when(restTemplate.postForObject(
            eq(config.getEndpoint()),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(response);

        // When
        CompletableFuture<Message> future = chatClient.callAsync(prompt);
        Message result = future.get(5, TimeUnit.SECONDS);

        // Then
        verify(restTemplate).postForObject(
            eq(config.getEndpoint()),
            any(HttpEntity.class),
            eq(Map.class)
        );

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Test response");
        assertThat(result.getRole()).isEqualTo(Message.Role.ASSISTANT);
    }

    @Test
    void shouldHandleSystemPrompt() {
        // Given
        Prompt prompt = Prompt.builder()
            .systemPrompt("System instruction")
            .userInput("User input")
            .build();

        Map<String, Object> response = Map.of(
            "output", Map.of(
                "text", "Test response"
            )
        );

        when(restTemplate.postForObject(
            eq(config.getEndpoint()),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(response);

        // When
        Message result = chatClient.call(prompt);

        // Then
        verify(restTemplate).postForObject(
            eq(config.getEndpoint()),
            requestCaptor.capture(),
            eq(Map.class)
        );

        HttpEntity<Map<String, Object>> request = requestCaptor.getValue();
        @SuppressWarnings("unchecked")
        List<Map<String, String>> messages = (List<Map<String, String>>) request.getBody().get("messages");
        
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0))
            .containsEntry("role", "system")
            .containsEntry("content", "System instruction");
        assertThat(messages.get(1))
            .containsEntry("role", "user")
            .containsEntry("content", "User input");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Test response");
    }

    @Test
    void shouldReturnCorrectConfig() {
        // When
        Map<String, Object> result = chatClient.getConfig();

        // Then
        assertThat(result)
            .containsEntry("provider", "alibaba")
            .containsEntry("model", config.getModel())
            .containsEntry("endpoint", config.getEndpoint());
    }

    @Test
    void shouldHandleApiError() {
        // Given
        Prompt prompt = Prompt.of("Test prompt");
        when(restTemplate.postForObject(
            eq(config.getEndpoint()),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenThrow(new RuntimeException("API Error"));

        // When/Then
        assertThatThrownBy(() -> chatClient.call(prompt))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to call Alibaba model");
    }
} 