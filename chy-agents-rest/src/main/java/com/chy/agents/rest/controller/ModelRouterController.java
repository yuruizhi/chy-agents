package com.chy.agents.rest.controller;

import com.chy.agents.core.router.ModelRouter;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 模型路由的REST控制器
 */
@RestController
@RequestMapping("/api/v1/router")
@RequiredArgsConstructor
public class ModelRouterController {

    private final ModelRouter modelRouter;

    /**
     * 获取可用的模型提供商
     *
     * @return 可用模型提供商列表
     */
    @GetMapping("/providers")
    public Map<String, Object> getProviders() {
        return modelRouter.getAvailableProviders();
    }

    /**
     * 将聊天请求路由到特定提供商
     *
     * @param provider 要使用的提供商
     * @param prompt 用户提示内容
     * @return 模型响应
     */
    @PostMapping("/{provider}/chat")
    public String routeChat(
            @PathVariable String provider,
            @RequestBody String prompt) {
        ChatClient client = modelRouter.selectClient(provider);
        return client.prompt()
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 使用默认提供商路由聊天请求
     *
     * @param prompt 用户提示内容
     * @return 模型响应
     */
    @PostMapping("/chat")
    public String routeDefaultChat(@RequestBody String prompt) {
        ChatClient client = modelRouter.selectDefaultClient();
        return client.prompt()
                .user(prompt)
                .call()
                .content();
    }
} 