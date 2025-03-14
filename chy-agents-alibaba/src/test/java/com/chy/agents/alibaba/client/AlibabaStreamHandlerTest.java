package com.chy.agents.alibaba.client;

import com.chy.agents.core.chat.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlibabaStreamHandlerTest {
    
    private static final String TEST_ENDPOINT = "https://test-endpoint";
    
    private Map<String, String> headers;
    private AlibabaStreamHandler streamHandler;
    
    @BeforeEach
    void setUp() {
        headers = new HashMap<>();
        headers.put("Authorization", "Bearer test-token");
        headers.put("Content-Type", "application/json");
        
        streamHandler = new AlibabaStreamHandler(TEST_ENDPOINT, headers);
    }
    
    @Test
    void shouldHandleStreamingEvents() throws InterruptedException {
        // Given
        CountDownLatch completionLatch = new CountDownLatch(1);
        AtomicReference<Message> lastMessage = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();
        
        streamHandler
            .onMessage(message -> {
                lastMessage.set(message);
            })
            .onError(error::set)
            .onComplete(completionLatch::countDown);
            
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "qwen-max");
        requestBody.put("messages", List.of(
            Map.of("role", "user", "content", "Test input")
        ));
        
        // When
        List<Message> messages = streamHandler.stream(requestBody);
        
        // Then
        boolean completed = completionLatch.await(5, TimeUnit.SECONDS);
        assertThat(completed).isTrue();
        assertThat(error.get()).isNull();
        assertThat(messages).isNotEmpty();
        assertThat(lastMessage.get()).isNotNull();
    }
    
    @Test
    void shouldHandleStreamingError() throws InterruptedException {
        // Given
        CountDownLatch errorLatch = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();
        
        streamHandler
            .onError(e -> {
                error.set(e);
                errorLatch.countDown();
            });
            
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "invalid-model");
        
        // When
        List<Message> messages = streamHandler.stream(requestBody);
        
        // Then
        boolean errorOccurred = errorLatch.await(5, TimeUnit.SECONDS);
        assertThat(errorOccurred).isTrue();
        assertThat(error.get()).isNotNull();
        assertThat(messages).isEmpty();
    }
    
    @Test
    void shouldHandleInvalidEventData() throws InterruptedException {
        // Given
        CountDownLatch completionLatch = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();
        
        streamHandler
            .onError(error::set)
            .onComplete(completionLatch::countDown);
            
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "qwen-max");
        
        // When
        List<Message> messages = streamHandler.stream(requestBody);
        
        // Then
        boolean completed = completionLatch.await(5, TimeUnit.SECONDS);
        assertThat(completed).isTrue();
        assertThat(error.get()).isNull();
        assertThat(messages).isEmpty();
    }
    
    @Test
    void shouldHandleMultipleEvents() throws InterruptedException {
        // Given
        CountDownLatch completionLatch = new CountDownLatch(1);
        List<Message> receivedMessages = new java.util.concurrent.CopyOnWriteArrayList<>();
        
        streamHandler
            .onMessage(receivedMessages::add)
            .onComplete(completionLatch::countDown);
            
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "qwen-max");
        requestBody.put("messages", List.of(
            Map.of("role", "user", "content", "Test input")
        ));
        
        // When
        List<Message> messages = streamHandler.stream(requestBody);
        
        // Then
        boolean completed = completionLatch.await(5, TimeUnit.SECONDS);
        assertThat(completed).isTrue();
        assertThat(messages).hasSameSizeAs(receivedMessages);
        assertThat(messages).isNotEmpty();
    }
} 