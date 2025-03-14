package com.chy.agents.openai.client;

import com.chy.agents.core.chat.message.BaseMessage;
import com.chy.agents.core.chat.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * OpenAI stream handler for processing streaming responses
 */
@Slf4j
public class OpenAiStreamHandler {
    
    private final WebClient webClient;
    private final Map<String, String> headers;
    private final List<Message> messages;
    private final AtomicReference<StringBuilder> currentMessage;
    private Consumer<Message> onMessage;
    private Consumer<Throwable> onError;
    private Runnable onComplete;
    
    public OpenAiStreamHandler(String endpoint, Map<String, String> headers) {
        this.webClient = WebClient.builder()
            .baseUrl(endpoint)
            .build();
        this.headers = headers;
        this.messages = new CopyOnWriteArrayList<>();
        this.currentMessage = new AtomicReference<>(new StringBuilder());
    }
    
    public OpenAiStreamHandler onMessage(Consumer<Message> onMessage) {
        this.onMessage = onMessage;
        return this;
    }
    
    public OpenAiStreamHandler onError(Consumer<Throwable> onError) {
        this.onError = onError;
        return this;
    }
    
    public OpenAiStreamHandler onComplete(Runnable onComplete) {
        this.onComplete = onComplete;
        return this;
    }
    
    public List<Message> stream(Map<String, Object> requestBody) {
        messages.clear();
        currentMessage.set(new StringBuilder());
        
        webClient.post()
            .headers(httpHeaders -> headers.forEach(httpHeaders::add))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToFlux(ServerSentEvent.class)
            .timeout(Duration.ofSeconds(60))
            .doOnNext(this::handleEvent)
            .doOnError(this::handleError)
            .doOnComplete(this::handleComplete)
            .onErrorResume(e -> {
                handleError(e);
                return Mono.empty();
            })
            .subscribe();
        
        return messages;
    }
    
    private void handleEvent(ServerSentEvent<?> event) {
        try {
            if (event.data() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) event.data();
                
                if (data.containsKey("choices")) {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) data.get("choices");
                    if (!choices.isEmpty()) {
                        Map<String, Object> choice = choices.get(0);
                        Map<String, Object> delta = (Map<String, Object>) choice.get("delta");
                        
                        if (delta != null && delta.containsKey("content")) {
                            String content = (String) delta.get("content");
                            if (content != null) {
                                StringBuilder current = currentMessage.get();
                                current.append(content);
                                
                                Message message = BaseMessage.assistantMessage(current.toString());
                                messages.clear(); // Clear previous incremental messages
                                messages.add(message);
                                
                                if (onMessage != null) {
                                    onMessage.accept(message);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to handle streaming event", e);
            handleError(e);
        }
    }
    
    private void handleError(Throwable error) {
        log.error("Streaming error", error);
        if (onError != null) {
            onError.accept(error);
        }
    }
    
    private void handleComplete() {
        log.debug("Streaming completed");
        if (onComplete != null) {
            onComplete.run();
        }
    }
} 