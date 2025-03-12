package com.chy.agents.rag.embeddings;

import org.springframework.ai.document.Document;
import java.util.List;

public interface ChunkingStrategy {
    List<Document> chunk(Document document, int maxChunkSize, int overlap);
}

// 实现类示例
package com.chy.agents.rag.embeddings;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@Component
public class TextChunkingStrategy implements ChunkingStrategy {
    @Override
    public List<Document> chunk(Document document, int maxChunkSize, int overlap) {
        String content = document.getContent();
        Map<String, Object> metadata = document.getMetadata();
        
        List<Document> chunks = new ArrayList<>();
        // 分块逻辑实现
        // ...
        
        return chunks;
    }
} 