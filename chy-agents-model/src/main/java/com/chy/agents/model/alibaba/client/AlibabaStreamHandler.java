package com.chy.agents.model.alibaba.client;

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
 * 阿里云通义流式处理器
 */
@Slf4j
public class AlibabaStreamHandler {
    
    private final WebClient webClient;
    private final Map<String, String> headers;
    private final List<Message> messages;
    private final AtomicReference<StringBuilder> currentMessage;
    private Consumer<Message> onMessage;
    private Consumer<Throwable> onError;
    private Runnable onComplete;
    
    public AlibabaStreamHandler(String endpoint, Map<String, String> headers) {
        this.webClient = WebClient.builder()
            .baseUrl(endpoint)
            .defaultHeaders(httpHeaders -> headers.forEach(httpHeaders::add))
            .build();
        this.headers = headers;
        this.messages = new CopyOnWriteArrayList<>();
        this.currentMessage = new AtomicReference<>(new StringBuilder());
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
        messages.clear();
        currentMessage.set(new StringBuilder());
        
        webClient.post()
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
                
                if (data.containsKey("output")) {
                    Map<String, Object> output = (Map<String, Object>) data.get("output");
                    String text = (String) output.get("text");
                    
                    // 处理增量内容
                    if (text != null) {
                        StringBuilder current = currentMessage.get();
                        current.append(text);
                        
                        Message message = BaseMessage.assistantMessage(current.toString());
                        
                        // 使用线程安全方式更新消息列表
                        synchronized (messages) {
                            messages.clear();
                            messages.add(message);
                        }
                        
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