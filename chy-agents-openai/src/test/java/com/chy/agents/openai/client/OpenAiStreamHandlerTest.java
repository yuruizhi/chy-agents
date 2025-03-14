package com.chy.agents.openai.client;

import com.chy.agents.core.chat.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenAiStreamHandlerTest {

    private OpenAiStreamHandler streamHandler;
    private Map<String, String> headers;

    @BeforeEach
    void setUp() {
        headers = new HashMap<>();
        headers.put("Authorization", "Bearer test-api-key");
        headers.put("Content-Type", "application/json");
        
        streamHandler = new OpenAiStreamHandler("https://api.openai.com/v1/chat/completions", headers);
    }

    @Test
    void testHandleEvent() throws Exception {
        // Create a latch to wait for the message
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Message> receivedMessage = new AtomicReference<>();
        
        // Set up the message handler
        streamHandler.onMessage(message -> {
            receivedMessage.set(message);
            latch.countDown();
        });
        
        // Create a test event
        ServerSentEvent<Map<String, Object>> event = ServerSentEvent.builder(createTestEventData("Hello")).build();
        
        // Call the handleEvent method directly using reflection
        java.lang.reflect.Method method = OpenAiStreamHandler.class.getDeclaredMethod("handleEvent", ServerSentEvent.class);
        method.setAccessible(true);
        method.invoke(streamHandler, event);
        
        // Wait for the message to be processed
        assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();
        
        // Verify the message
        Message message = receivedMessage.get();
        assertThat(message).isNotNull();
        assertThat(message.getContent()).isEqualTo("Hello");
        assertThat(message.getRole()).isEqualTo(Message.Role.ASSISTANT);
        
        // Create a second event to test appending
        ServerSentEvent<Map<String, Object>> event2 = ServerSentEvent.builder(createTestEventData(" world!")).build();
        
        // Reset the latch for the second test
        final CountDownLatch latch2 = new CountDownLatch(1);
        
        // Set up the message handler again with the new latch
        streamHandler.onMessage(message2 -> {
            receivedMessage.set(message2);
            latch2.countDown();
        });
        
        // Call handleEvent again
        method.invoke(streamHandler, event2);
        
        // Wait for the message to be processed
        assertThat(latch2.await(1, TimeUnit.SECONDS)).isTrue();
        
        // Verify the message has been appended
        message = receivedMessage.get();
        assertThat(message).isNotNull();
        assertThat(message.getContent()).isEqualTo("Hello world!");
        assertThat(message.getRole()).isEqualTo(Message.Role.ASSISTANT);
    }

    @Test
    void testHandleError() throws Exception {
        // Create a latch to wait for the error
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> receivedError = new AtomicReference<>();
        
        // Set up the error handler
        streamHandler.onError(error -> {
            receivedError.set(error);
            latch.countDown();
        });
        
        // Create a test error
        Exception testError = new RuntimeException("Test error");
        
        // Call the handleError method directly using reflection
        java.lang.reflect.Method method = OpenAiStreamHandler.class.getDeclaredMethod("handleError", Throwable.class);
        method.setAccessible(true);
        method.invoke(streamHandler, testError);
        
        // Wait for the error to be processed
        assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();
        
        // Verify the error
        Throwable error = receivedError.get();
        assertThat(error).isNotNull();
        assertThat(error.getMessage()).isEqualTo("Test error");
    }

    @Test
    void testHandleComplete() throws Exception {
        // Create a latch to wait for completion
        CountDownLatch latch = new CountDownLatch(1);
        
        // Set up the completion handler
        streamHandler.onComplete(latch::countDown);
        
        // Call the handleComplete method directly using reflection
        java.lang.reflect.Method method = OpenAiStreamHandler.class.getDeclaredMethod("handleComplete");
        method.setAccessible(true);
        method.invoke(streamHandler);
        
        // Wait for completion
        assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();
    }

    private Map<String, Object> createTestEventData(String content) {
        Map<String, Object> delta = new HashMap<>();
        delta.put("content", content);
        
        Map<String, Object> choice = new HashMap<>();
        choice.put("delta", delta);
        
        Map<String, Object> data = new HashMap<>();
        data.put("choices", List.of(choice));
        
        return data;
    }
} 