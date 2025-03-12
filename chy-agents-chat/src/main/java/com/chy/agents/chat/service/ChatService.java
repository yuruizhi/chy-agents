package com.chy.agents.chat.service;


import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.ArrayList;

@Service
public class ChatService {

    private static final String DEFAULT_PROMPT = "你好，介绍下你自己！";

    @Resource
    private ChatClient chatClient;

    private final ChatModel chatModel;



    public ChatService(ChatModel chatModel) {

        this.chatModel = chatModel;

        // 构造时，可以设置 ChatClient 的参数
        // {@link org.springframework.ai.chat.client.ChatClient};
        this.chatClient = ChatClient.builder(chatModel)
                // 实现 Chat Memory 的 Advisor
                // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory())
                )
                // 实现 Logger 的 Advisor
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(
                        OpenAiChatOptions.builder()
                                .topP(0.7)
                                .build()
                )
                .build();
    }
    

    
    public String chat(String userInput, List<Message> chatHistory) {
        List<Message> messages = new ArrayList<>(chatHistory);
        messages.add(new UserMessage(userInput));
        
        Prompt prompt = new Prompt(messages);

        return chatClient.prompt(prompt).call().content();
    }

    public Flux<String> streamChat(String userInput, List<Message> chatHistory) {
        List<Message> messages = new ArrayList<>(chatHistory);
        messages.add(new UserMessage(userInput));

        Prompt prompt = new Prompt(messages);
        return chatClient.prompt(prompt).stream().content();
    }
} 