package com.chy.agents.rag.service;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RagService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    
    @Autowired
    public RagService(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }
    
    public String query(String userQuery) {
        // 1. 从向量存储中检索相关文档
        List<Document> relevantDocs = vectorStore.similaritySearch(
            SearchRequest.query(userQuery).withTopK(3)
        );
        
        // 2. 构建上下文
        StringBuilder context = new StringBuilder();
        for (Document doc : relevantDocs) {
            context.append(doc.getContent()).append("\n\n");
        }
        
        // 3. 构建提示
        String promptTemplate = "基于以下信息回答问题。如果无法从提供的信息中找到答案，请说明你不知道。\n\n" +
                               "信息: " + context + "\n\n" +
                               "问题: " + userQuery;
        
        // 4. 调用LLM
        UserMessage userMessage = new UserMessage(promptTemplate);
        Prompt prompt = new Prompt(userMessage);
        ChatResponse response = chatClient.call(prompt);
        
        return response.getResult().getOutput().getContent();
    }
    
    public void addDocumentToKnowledgeBase(String content, Map<String, Object> metadata) {
        Document document = new Document(content, metadata);
        vectorStore.add(List.of(document));
    }
} 