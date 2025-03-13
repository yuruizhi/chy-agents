package com.chy.agents.common.exception;

/**
 * 代理系统基础异常
 */
public class AgentException extends RuntimeException {
    
    private final ErrorCode errorCode;
    
    public AgentException(String message) {
        super(message);
        this.errorCode = ErrorCode.UNKNOWN_ERROR;
    }
    
    public AgentException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.UNKNOWN_ERROR;
    }
    
    public AgentException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public AgentException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    /**
     * 错误码枚举
     */
    public enum ErrorCode {
        UNKNOWN_ERROR(10000, "未知错误"),
        CONFIG_ERROR(10001, "配置错误"),
        NETWORK_ERROR(10002, "网络错误"),
        API_ERROR(10003, "API调用错误"),
        TIMEOUT_ERROR(10004, "超时错误"),
        INVALID_INPUT(10005, "无效输入"),
        MODEL_ERROR(10006, "模型错误"),
        RESOURCE_ERROR(10007, "资源错误");
        
        private final int code;
        private final String description;
        
        ErrorCode(int code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public int getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
    }
} 