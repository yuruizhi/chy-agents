package com.chy.agents.common.utils;

import com.chy.agents.common.exception.AgentException;
import com.chy.agents.common.exception.AgentException.ErrorCode;

/**
 * Token工具类
 */
public class TokenUtils {
    
    private static final int AVERAGE_CHARS_PER_TOKEN = 4;
    private static final int MAX_TOKENS_DEFAULT = 4096;
    
    /**
     * 估算文本的token数量
     * 这是一个粗略的估算，实际token数可能因模型和具体内容而异
     *
     * @param text 输入文本
     * @return 估算的token数
     */
    public static int estimateTokenCount(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return (int) Math.ceil((double) text.length() / AVERAGE_CHARS_PER_TOKEN);
    }
    
    /**
     * 检查文本是否超过token限制
     *
     * @param text 输入文本
     * @param maxTokens 最大token数
     * @return 是否超过限制
     */
    public static boolean isExceedingTokenLimit(String text, int maxTokens) {
        return estimateTokenCount(text) > maxTokens;
    }
    
    /**
     * 截断文本以适应token限制
     *
     * @param text 输入文本
     * @param maxTokens 最大token数
     * @return 截断后的文本
     */
    public static String truncateToTokenLimit(String text, int maxTokens) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        int estimatedMaxLength = maxTokens * AVERAGE_CHARS_PER_TOKEN;
        if (text.length() <= estimatedMaxLength) {
            return text;
        }
        
        return text.substring(0, estimatedMaxLength);
    }
    
    /**
     * 验证文本token数量
     *
     * @param text 输入文本
     * @throws AgentException 如果超过默认限制
     */
    public static void validateTokenCount(String text) {
        validateTokenCount(text, MAX_TOKENS_DEFAULT);
    }
    
    /**
     * 验证文本token数量
     *
     * @param text 输入文本
     * @param maxTokens 最大token数
     * @throws AgentException 如果超过限制
     */
    public static void validateTokenCount(String text, int maxTokens) {
        if (isExceedingTokenLimit(text, maxTokens)) {
            throw new AgentException(
                ErrorCode.INVALID_INPUT,
                String.format("Text exceeds token limit of %d", maxTokens)
            );
        }
    }
} 