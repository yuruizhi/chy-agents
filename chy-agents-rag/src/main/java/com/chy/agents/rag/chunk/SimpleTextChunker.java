package com.chy.agents.rag.chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 简单文本分块实现
 * 基于固定大小和重叠区域的分块策略
 */
public class SimpleTextChunker implements TextChunker {

    private final int chunkSize;
    private final int chunkOverlap;
    private final Pattern splitPattern;
    
    /**
     * 构造函数
     * 
     * @param chunkSize 块大小（字符数）
     * @param chunkOverlap 块重叠大小（字符数）
     */
    public SimpleTextChunker(int chunkSize, int chunkOverlap) {
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
        this.splitPattern = Pattern.compile("(?<=\\.)\\s+|(?<=\\n)");
    }
    
    @Override
    public List<String> chunk(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        
        List<String> chunks = new ArrayList<>();
        
        // 先按句子或段落分割
        String[] sentences = splitPattern.split(text);
        StringBuilder currentChunk = new StringBuilder();
        
        for (String sentence : sentences) {
            // 如果当前句子加上现有内容小于块大小，直接添加
            if (currentChunk.length() + sentence.length() <= chunkSize) {
                currentChunk.append(sentence);
                currentChunk.append(" ");
            } else {
                // 如果当前块不为空，添加到结果列表
                if (currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString().trim());
                }
                
                // 对于超长句子，需要进一步分割
                if (sentence.length() > chunkSize) {
                    int start = 0;
                    while (start < sentence.length()) {
                        int end = Math.min(start + chunkSize, sentence.length());
                        chunks.add(sentence.substring(start, end));
                        start += chunkSize - chunkOverlap;
                    }
                    currentChunk = new StringBuilder();
                } else {
                    // 开始新的块
                    currentChunk = new StringBuilder(sentence);
                    currentChunk.append(" ");
                }
            }
        }
        
        // 添加最后一个块
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }
        
        return chunks;
    }
    
    @Override
    public List<DocumentChunk> chunkWithMetadata(String text, String documentId, String documentTitle) {
        List<String> chunks = chunk(text);
        List<DocumentChunk> documentChunks = new ArrayList<>(chunks.size());
        
        for (int i = 0; i < chunks.size(); i++) {
            documentChunks.add(new DocumentChunk(chunks.get(i), documentId, documentTitle, i));
        }
        
        return documentChunks;
    }
} 