package com.chy.agents.core.agent;

import com.chy.agents.core.chat.ChatClient;
import com.chy.agents.core.router.ModelRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Agent接口的基本实现类
 * 提供智能代理的核心功能实现
 */
public class BaseAgent implements Agent {

    private static final Logger logger = LoggerFactory.getLogger(BaseAgent.class);
    
    // 代理唯一标识符
    private final String id;
    
    // 代理名称
    private final String name;
    
    // 代理描述
    private final String description;
    
    // 代理配置
    private final AgentConfig config;
    
    // 代理当前状态
    private AgentStatus status = AgentStatus.INITIALIZED;
    
    // 代理能力集合
    private final List<String> capabilities = new ArrayList<>();
    
    // 代理工具集合
    private List<Tool> tools = new ArrayList<>();
    
    // 代理记忆
    private Memory memory;
    
    // 模型路由器
    @Autowired
    private ModelRouter modelRouter;
    
    // 上下文存储
    private final Map<String, Object> context = new ConcurrentHashMap<>();
    
    /**
     * 构造函数
     *
     * @param id 代理唯一标识符
     * @param name 代理名称
     * @param description 代理描述
     * @param config 代理配置
     */
    public BaseAgent(String id, String name, String description, AgentConfig config) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.config = config;
    }
    
    /**
     * 获取代理ID
     *
     * @return 代理唯一标识符
     */
    @Override
    public String getId() {
        return id;
    }
    
    /**
     * 获取代理名称
     *
     * @return 代理名称
     */
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * 获取代理描述
     *
     * @return 代理描述
     */
    @Override
    public String getDescription() {
        return description;
    }
    
    /**
     * 获取代理配置
     *
     * @return 代理配置对象
     */
    @Override
    public AgentConfig getConfig() {
        return config;
    }
    
    /**
     * 处理用户输入
     *
     * @param input 用户输入内容
     * @return 处理结果
     */
    @Override
    public String process(String input) {
        return process(input, Collections.emptyMap());
    }
    
    /**
     * 处理用户输入（带上下文）
     *
     * @param input 用户输入内容
     * @param contextParams 上下文参数
     * @return 处理结果
     */
    @Override
    public String process(String input, Map<String, Object> contextParams) {
        if (status != AgentStatus.RUNNING) {
            logger.warn("代理 [{}] 未处于运行状态，当前状态: {}", id, status);
            return "Agent is not running. Current status: " + status;
        }
        
        try {
            // 执行任务并返回结果内容
            AgentResponse response = execute(input, contextParams);
            return response.getContent();
        } catch (Exception e) {
            logger.error("代理 [{}] 处理请求失败", id, e);
            status = AgentStatus.ERROR;
            return "处理请求时发生错误: " + e.getMessage();
        }
    }
    
    /**
     * 同步执行任务
     *
     * @param input 输入参数
     * @param contextParams 上下文参数
     * @return 执行结果
     */
    @Override
    public AgentResponse execute(String input, Map<String, Object> contextParams) {
        try {
            // 更新上下文
            if (contextParams != null) {
                context.putAll(contextParams);
            }
            
            // 记录请求
            logger.info("代理 [{}] 收到执行请求: {}", id, input);
            
            // 获取合适的模型客户端
            ChatClient client = modelRouter.getClientForAgent(this);
            if (client == null) {
                throw new IllegalStateException("无法获取模型客户端");
            }
            
            // TODO: 实现完整的处理逻辑，包括：
            // 1. 构建提示词
            // 2. 调用模型
            // 3. 解析响应
            // 4. 处理工具调用等
            
            // 简单实现：直接返回模拟响应
            String response = "这是代理 " + name + " 的响应: " + input;
            
            logger.info("代理 [{}] 生成响应: {}", id, response);
            return AgentResponse.ofText(response);
            
        } catch (Exception e) {
            logger.error("代理 [{}] 执行请求失败", id, e);
            status = AgentStatus.ERROR;
            return AgentResponse.ofError(e.getMessage());
        }
    }
    
    /**
     * 异步执行任务
     *
     * @param input 输入参数
     * @param contextParams 上下文参数
     * @return 异步执行结果
     */
    @Override
    public CompletableFuture<AgentResponse> executeAsync(String input, Map<String, Object> contextParams) {
        return CompletableFuture.supplyAsync(() -> execute(input, contextParams))
                .orTimeout(30, TimeUnit.SECONDS); // 默认30秒超时
    }
    
    /**
     * 获取代理能力列表
     *
     * @return 能力列表
     */
    @Override
    public List<String> getCapabilities() {
        return Collections.unmodifiableList(capabilities);
    }
    
    /**
     * 检查代理是否具有指定能力
     *
     * @param capability 能力名称
     * @return 是否具有该能力
     */
    @Override
    public boolean hasCapability(String capability) {
        return capabilities.contains(capability);
    }
    
    /**
     * 添加代理能力
     *
     * @param capability 能力名称
     */
    public void addCapability(String capability) {
        capabilities.add(capability);
        logger.info("代理 [{}] 添加能力: {}", id, capability);
    }
    
    /**
     * 移除代理能力
     *
     * @param capability 能力名称
     */
    public void removeCapability(String capability) {
        capabilities.remove(capability);
        logger.info("代理 [{}] 移除能力: {}", id, capability);
    }
    
    /**
     * 获取代理状态
     *
     * @return 当前状态
     */
    @Override
    public AgentStatus getStatus() {
        return status;
    }
    
    /**
     * 启动代理
     */
    @Override
    public void start() {
        if (status == AgentStatus.RUNNING) {
            logger.info("代理 [{}] 已经在运行中", id);
            return;
        }
        
        logger.info("启动代理 [{}]", id);
        status = AgentStatus.RUNNING;
    }
    
    /**
     * 停止代理
     */
    @Override
    public void stop() {
        if (status == AgentStatus.STOPPED) {
            logger.info("代理 [{}] 已经停止", id);
            return;
        }
        
        logger.info("停止代理 [{}]", id);
        status = AgentStatus.STOPPED;
    }
    
    /**
     * 重置代理状态
     */
    @Override
    public void reset() {
        logger.info("重置代理 [{}]", id);
        context.clear();
        if (memory != null) {
            memory.clear();
        }
        status = AgentStatus.INITIALIZED;
    }
    
    /**
     * 获取代理工具列表
     *
     * @return 工具列表
     */
    @Override
    public List<Tool> getTools() {
        return tools;
    }
    
    /**
     * 设置代理工具列表
     *
     * @param tools 工具列表
     */
    @Override
    public void setTools(List<Tool> tools) {
        this.tools = tools;
    }
    
    /**
     * 获取代理记忆
     *
     * @return 记忆
     */
    @Override
    public Memory getMemory() {
        return memory;
    }
    
    /**
     * 设置代理记忆
     *
     * @param memory 记忆
     */
    @Override
    public void setMemory(Memory memory) {
        this.memory = memory;
    }
    
    /**
     * 获取模型路由器
     *
     * @return 模型路由器
     */
    protected ModelRouter getModelRouter() {
        return modelRouter;
    }
    
    /**
     * 设置模型路由器
     *
     * @param modelRouter 模型路由器
     */
    public void setModelRouter(ModelRouter modelRouter) {
        this.modelRouter = modelRouter;
    }
    
    /**
     * 获取上下文
     *
     * @return 上下文参数映射
     */
    protected Map<String, Object> getContext() {
        return context;
    }
} 