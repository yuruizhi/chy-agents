# CHY Agents - 模型集成

该模块为CHY Agents提供与各种LLM提供商和模型的集成。

## 子模块

- **chy-agents-model-openai**: 与OpenAI模型集成（GPT-3.5、GPT-4等）
- **chy-agents-model-alibaba**: 与阿里云模型集成（通义千问等）
- **chy-agents-model-deepseek**: 与DeepSeek模型集成
- **chy-agents-model-anthropic**: 与Anthropic Claude模型集成
- **chy-agents-model-google**: 与Google Gemini模型集成
- **chy-agents-model-private**: 与私有/本地模型集成（llama.cpp、vllm等）

## 核心特性

- **多模型支持**: 访问不同提供商的各种模型
- **统一API**: 跨不同模型提供商的通用接口
- **负载均衡**: 基于需求的智能模型路由
- **故障转移机制**: 自动重试和故障转移能力
- **流式支持**: 用于实时响应的流式API

## 使用示例

每个模型子模块可以独立使用，也可以通过核心模块的路由机制使用。

```java
@Service
public class ChatService {
    @Resource
    private ModelRouter modelRouter;
    
    public String chat(String input, String provider) {
        ChatClient client = modelRouter.selectClient(provider);
        return client.call(new Prompt(input))
                    .getResult()
                    .getOutput()
                    .getContent();
    }
} 