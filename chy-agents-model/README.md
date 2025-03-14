# CHY Agents - Model Integration

This module provides integration with various LLM providers and models for CHY Agents.

## Submodules

- **chy-agents-model-openai**: Integration with OpenAI models (GPT-3.5, GPT-4, etc.)
- **chy-agents-model-alibaba**: Integration with Alibaba Cloud models (Qwen, etc.)
- **chy-agents-model-deepseek**: Integration with DeepSeek models
- **chy-agents-model-anthropic**: Integration with Anthropic Claude models
- **chy-agents-model-google**: Integration with Google Gemini models
- **chy-agents-model-private**: Integration with private/local models (llama.cpp, vllm, etc.)

## Key Features

- **Multi-model Support**: Access to a variety of models from different providers
- **Unified API**: Common interface across different model providers
- **Load Balancing**: Smart routing between models based on requirements
- **Fallback Mechanisms**: Automatic retry and failover capabilities
- **Streaming Support**: Streaming API for real-time responses

## Usage

Each model submodule can be used independently or through the core module's routing mechanism.

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