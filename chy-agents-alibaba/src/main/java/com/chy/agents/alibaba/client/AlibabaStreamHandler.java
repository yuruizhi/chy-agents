package com.chy.agents.alibaba.client;

import com.chy.agents.core.chat.message.BaseMessage;
import com.chy.agents.core.chat.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * 阿里云通义流式处理器
 */
@Slf4j
public class AlibabaStreamHandler {
    
    private final WebClient webClient;
    private final Map<String, String> headers;
    private final List<Message> messages;
    private final StringBuilder currentMessage;
    private Consumer<Message> onMessage;
    private Consumer<Throwable> onError;
    private Runnable onComplete;
    
    public AlibabaStreamHandler(String endpoint, Map<String, String> headers) {
        this.webClient = WebClient.create(endpoint);
        this.headers = headers;
        this.messages = new CopyOnWriteArrayList<>();
        this.currentMessage = new StringBuilder();
    }
    
    public AlibabaStreamHandler onMessage(Consumer<Message> onMessage) {
        this.onMessage = onMessage;
        return this;
    }
    
    public AlibabaStreamHandler onError(Consumer<Throwable> onError) {
        this.onError = onError;
        return this;
    }
    
    public AlibabaStreamHandler onComplete(Runnable onComplete) {
        this.onComplete = onComplete;
        return this;
    }
    
    public List<Message> stream(Map<String, Object> requestBody) {
        webClient.post()
            .headers(httpHeaders -> headers.forEach(httpHeaders::add))
            .bodyValue(requestBody)
            .retrieve()
            .bodyToFlux(ServerSentEvent.class)
            .subscribe(
                this::handleEvent,
                this::handleError,
                this::handleComplete
            );
        
        return messages;
    }
    
    private void handleEvent(ServerSentEvent<?> event) {
        try {
            if (event.data() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) event.data();
                
                if (data.containsKey("output")) {
                    Map<String, Object> output = (Map<String, Object>) data.get("output");
                    String text = (String) output.get("text");
                    
                    // 处理增量内容
                    if (text != null) {
                        currentMessage.append(text);
                        Message message = BaseMessage.assistantMessage(currentMessage.toString());
                        messages.add(message);
                        
                        if (onMessage != null) {
                            onMessage.accept(message);
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