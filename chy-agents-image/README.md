# CHY Agents - 图像处理模块

该模块提供图像生成和图像分析功能，是CHY Agents智能代理系统的多模态能力的重要组成部分。

## 主要功能

1. **图像生成** - 基于文本提示生成高质量图像
   - 默认使用StabilityAI的Stable Diffusion XL模型
   - 支持自定义图像尺寸、数量和模型
   - 基于Spring AI的标准化接口实现，便于未来扩展其他提供商

2. **图像分析** - 分析图像内容并生成文本描述
   - 使用OpenAI的GPT-4-Vision模型分析图像
   - 支持自定义分析提示，获取针对性描述

## 模块架构

该模块基于Spring AI 1.0.0-M5版本构建，核心组件包括：

- **ImageModel接口** - 使用Spring AI提供的统一接口调用不同图像生成服务
- **ChatClient** - 用于图像分析的多模态大语言模型客户端
- **自动配置** - 通过Spring Boot的自动配置机制，简化集成与使用
- **REST API** - 提供标准的HTTP接口，用于图像生成和分析

## 快速开始

### 1. 配置

在`application.properties`或`application.yml`中添加以下配置：

```properties
# 图像服务配置
chy.agents.image.enabled=true
chy.agents.image.api-key=${STABILITY_API_KEY:your_stability_key_here}

# OpenAI配置（用于图像分析）
spring.ai.openai.api-key=${OPENAI_API_KEY:your_openai_key_here}
```

您也可以直接引入application-image.properties：

```properties
spring.profiles.include=image
```

### 2. 使用图像生成服务

```java
@Autowired
private ImageGenerationService imageGenerationService;

// 使用默认参数生成图像
byte[] imageData = imageGenerationService.generateImageAsBytes("一只在草地上奔跑的金毛猎犬");

// 使用自定义参数生成图像
byte[] customImage = imageGenerationService.generateImageAsBytes(
    "一座未来风格的城市",  // 提示文本
    1024,  // 宽度
    768,   // 高度
    "stable-diffusion-xl-1024-v1-0"  // 模型
);
```

### 3. 使用图像分析服务

```java
@Autowired
private ImageAnalysisService imageAnalysisService;

// 分析图像
byte[] imageData = Files.readAllBytes(Paths.get("path/to/image.jpg"));
String analysis = imageAnalysisService.analyzeImage(
    imageData,  // 图像数据
    "这张图片中的人物穿着什么服装?"  // 自定义分析提示
);
```

### 4. 使用REST API

**生成图像：**

```http
POST /api/image/generate
Content-Type: application/json

{
    "prompt": "一只在草地上奔跑的金毛猎犬",
    "width": 1024,
    "height": 1024
}
```

**分析图像：**

```http
POST /api/image/analyze
Content-Type: multipart/form-data

file: [图像文件]
prompt: "这张图片中有什么？"
```

## 配置参数

| 参数 | 描述 | 默认值 |
|------|------|--------|
| chy.agents.image.enabled | 是否启用图像服务 | true |
| chy.agents.image.provider | 图像生成提供商 | stabilityai |
| chy.agents.image.api-key | API密钥 | YOUR_API_KEY |
| chy.agents.image.width | 默认图像宽度 | 1024 |
| chy.agents.image.height | 默认图像高度 | 1024 |
| chy.agents.image.number-of-images | 默认图像生成数量 | 1 |
| chy.agents.image.model | 默认模型 | stable-diffusion-xl-1024-v1-0 |
| chy.agents.image.creativity | 创新度 | 0.7 |

## 依赖关系

该模块依赖于：
- Spring AI Core (1.0.0-M5)
- Spring AI StabilityAI (用于图像生成)
- Spring AI OpenAI (用于图像分析)
- Spring Boot Web
- Spring Boot Actuator
- CHY Agents Common & Core

## 扩展开发

### 添加新的图像生成提供商

您可以通过实现Spring AI的ImageModel接口来添加新的图像生成提供商：

```java
@Service
@ConditionalOnProperty(prefix = "chy.agents.image", name = "provider", havingValue = "your-provider")
public class YourProviderImageModel implements ImageModel {
    
    @Override
    public ImageResponse call(ImagePrompt prompt) {
        // 实现您的图像生成逻辑
    }
}
```

注册您的提供商到配置类：

```java
@Bean
@ConditionalOnProperty(prefix = "chy.agents.image", name = "provider", havingValue = "your-provider")
public ImageModel yourProviderImageModel(YourProviderProperties properties) {
    // 创建并返回您的ImageModel实现
}
``` 