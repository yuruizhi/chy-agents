package com.chy.agents.image.service;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Collections;

/**
 * 图像分析服务
 * 使用OpenAI的GPT-4-Vision或其他支持视觉的大语言模型分析图像
 */
@Service
public class ImageAnalysisService {

    private final ChatClient chatClient;

    @Autowired
    public ImageAnalysisService(@Qualifier("openAiChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 分析图像内容
     *
     * @param imageData 图像的字节数据
     * @param prompt    提示文本，即询问模型的问题
     * @return 分析结果文本
     */
    public String analyzeImage(byte[] imageData, String prompt) {
        // 创建媒体对象，图像数据使用base64编码
        String base64Data = Base64.getEncoder().encodeToString(imageData);
        Media mediaData = new Media(Media.MediaType.PNG, base64Data);

        // 创建用户消息，包含文本和图像
        UserMessage userMessage = new UserMessage(
            prompt, 
            Collections.singletonList(mediaData)
        );

        // 创建提示并发送到聊天模型
        Prompt promptRequest = new Prompt(Collections.singletonList((Message) userMessage));
        ChatResponse response = chatClient.call(promptRequest);

        // 返回模型生成的文本
        return response.getResult().getOutput().getContent();
    }
} 