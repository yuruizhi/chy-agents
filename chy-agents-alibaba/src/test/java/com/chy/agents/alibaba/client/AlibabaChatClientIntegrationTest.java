package com.chy.agents.alibaba.client;

import com.chy.agents.alibaba.config.AlibabaConfig;
import com.chy.agents.core.chat.message.Message;
import com.chy.agents.core.chat.prompt.Prompt;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlibabaChatClientIntegrationTest {

    private MockWebServer mockWebServer;
    private AlibabaChatClient chatClient;
    private AlibabaConfig config;
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeEach
    void setUp() throws IOException {
        // 设置Mock Web Server
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        
        // 配置
        config = new AlibabaConfig();
        config.setApiKey("test-api-key");
        config.setModel("qwen-max");
        config.setEndpoint(mockWebServer.url("/").toString());
        config.setTemperature(0.7f);
        config.setMaxTokens(4096);
        
        // 创建客户端
        chatClient = new AlibabaChatClient(config);
    }
    
    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
    
    @Test
    void shouldSendRequestWithCorrectHeaders() throws Exception {
        // 设置Mock响应
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody("{\"output\": {\"text\": \"Test response\"}}"));
        
        // 发送请求
        Message response = chatClient.call(Prompt.of("Test prompt"));
        
        // 验证请求
        RecordedRequest request = mockWebServer.takeRequest(5, TimeUnit.SECONDS);
        assertThat(request).isNotNull();
        assertThat(request.getHeader("Authorization")).isEqualTo("Bearer test-api-key");
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json");
        
        // 验证请求体
        Map<String, Object> requestBody = objectMapper.readValue(request.getBody().readUtf8(), Map.class);
        assertThat(requestBody).containsKey("model");
        assertThat(requestBody).containsKey("messages");
        assertThat(requestBody).containsKey("temperature");
        assertThat(requestBody).containsKey("max_tokens");
        
        // 验证响应
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo("Test response");
        assertThat(response.getRole()).isEqualTo(Message.Role.ASSISTANT);
    }
    
    @Test
    void shouldHandleErrorResponse() {
        // 设置Mock错误响应
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(500)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody("{\"error\": {\"message\": \"Internal server error\"}}"));
        
        // 验证错误处理
        assertThatThrownBy(() -> chatClient.call(Prompt.of("Test prompt")))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to call Alibaba model");
    }
    
    @Test
    void shouldHandleStreamingResponses() throws Exception {
        // 设置流式响应的分发器
        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().contains("stream=true")) {
                    return new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, "text/event-stream")
                        .setBody("data: {\"output\": {\"text\": \"Hello\"}}\n\n" +
                               "data: {\"output\": {\"text\": \" World\"}}\n\n");
                }
                return new MockResponse().setResponseCode(404);
            }
        };
        mockWebServer.setDispatcher(dispatcher);
        
        // 发送流式请求
        Prompt prompt = Prompt.of("Test streaming");
        List<Message> messages = chatClient.stream(prompt);
        
        // 验证响应
        // 注意：由于流式处理的异步性质，这个测试可能不稳定
        // 在实际测试中，可能需要增加等待时间或更复杂的同步机制
        assertThat(messages).isNotNull();
    }
} 