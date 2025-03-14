package com.chy.agents.rag.chunk;

import java.util.List;

/**
 * 文本分块接口
 * 负责将文本按照一定规则分割成小块
 */
public interface TextChunker {

    /**
     * 将文本分块
     * 
     * @param text 待分块的文本
     * @return 分块后的文本列表
     */
    List<String> chunk(String text);
    
    /**
     * 将文本分块，并添加元数据
     * 
     * @param text 待分块的文本
     * @param metadata 元数据
     * @return 分块后的文本和元数据列表
     */
    List<DocumentChunk> chunkWithMetadata(String text, String documentId, String documentTitle);
    
    /**
     * 文档分块结果
     */
    class DocumentChunk {
        private final String content;
        private final String documentId;
        private final String documentTitle;
        private final int chunkIndex;
        
        public DocumentChunk(String content, String documentId, String documentTitle, int chunkIndex) {
            this.content = content;
            this.documentId = documentId;
            this.documentTitle = documentTitle;
            this.chunkIndex = chunkIndex;
        }
        
        public String getContent() {
            return content;
        }
        
        public String getDocumentId() {
            return documentId;
        }
        
        public String getDocumentTitle() {
            return documentTitle;
        }
        
        public int getChunkIndex() {
            return chunkIndex;
        }
    }
} 