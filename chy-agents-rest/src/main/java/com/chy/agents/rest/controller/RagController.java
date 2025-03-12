package com.chy.agents.rest.controller;


import com.chy.agents.rag.service.RagService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    @Resource
    private RagService ragService;

    
    @PostMapping("/query")
    public Map<String, String> query(@RequestBody Map<String, String> request) {
        String userQuery = request.get("query");
        String response = ragService.query(userQuery);
        
        return Map.of("response", response);
    }
    
    @PostMapping("/document")
    public Map<String, String> addDocument(@RequestBody Map<String, Object> request) {
        String content = (String) request.get("content");
        Map<String, Object> metadata = (Map<String, Object>) request.getOrDefault("metadata", Map.of());
        
        ragService.addDocumentToKnowledgeBase(content, metadata);
        
        return Map.of("status", "success", "message", "Document added to knowledge base");
    }
} 