# CHY Agents - 图像处理模块

该模块提供图像生成和图像分析功能，是CHY Agents智能代理系统的多模态能力的重要组成部分。

## 主要功能

1. **图像生成** - 基于文本提示生成高质量图像
   - 默认使用StabilityAI的Stable Diffusion XL模型
   - 支持自定义图像尺寸和创新度

2. **图像分析** - 分析图像内容并生成文本描述
   - 使用支持视觉能力的大语言模型分析图像
   - 支持自定义分析提示

## 快速开始

### 1. 配置

在`application.properties`或`application.yml`中添加以下配置：

```properties
# 图像服务配置
chy.agents.image.enabled=true
chy.agents.image.api-key=YOUR_STABILITY_AI_API_KEY

# OpenAI配置（用于图像分析）
spring.ai.openai.api-key=YOUR_OPENAI_API_KEY
```

### 2. 使用图像生成服务

```java
@Autowired
private ImageGenerationService imageGenerationService;

// 生成图像
byte[] imageData = imageGenerationService.generateImageAsBytes("一只在草地上奔跑的金毛猎犬");
```

### 3. 使用图像分析服务

```java
@Autowired
private ImageAnalysisService imageAnalysisService;

// 分析图像
byte[] imageData = Files.readAllBytes(Paths.get("path/to/image.jpg"));
String analysis = imageAnalysisService.analyzeImage(imageData, "这张图片中有什么？");
```

### 4. 使用REST API

生成图像：
```
POST /api/image/generate
{
    "prompt": "一只在草地上奔跑的金毛猎犬",
    "count": 1,
    "width": 1024,
    "height": 1024
}
```

分析图像：
```
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
| chy.agents.image.api-key | API密钥 | 无（必须配置） |
| chy.agents.image.width | 默认图像宽度 | 1024 |
| chy.agents.image.height | 默认图像高度 | 1024 |
| chy.agents.image.number-of-images | 默认图像生成数量 | 1 |
| chy.agents.image.model | 默认模型 | stable-diffusion-xl-1024-v1-0 |
| chy.agents.image.creativity | 创新度 | 0.7 |

## 依赖关系

该模块依赖于：
- Spring AI Stability AI
- Spring AI OpenAI（用于图像分析）
- Spring Boot Web
- Spring Boot Actuator
- CHY Agents Common 