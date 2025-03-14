# CHY Agents 多模态处理模块

## 模块介绍

多模态处理模块是CHY Agents系统中负责处理多种模态数据的核心组件，包括图像、音频、视频和多模态融合等功能。

## 模块结构

该模块按照功能拆分为以下子模块：

- **chy-agents-multimodal-image**: 图像处理模块，支持图像生成、分析和理解
- **chy-agents-multimodal-audio**: 音频处理模块，支持语音识别和文本转语音
- **chy-agents-multimodal-video**: 视频处理模块，支持视频分析和生成
- **chy-agents-multimodal-fusion**: 多模态融合模块，支持跨模态的理解与生成

## 迁移说明

从版本1.0.0-M6开始，原独立的`chy-agents-image`模块已合并到`chy-agents-multimodal-image`中，以符合系统架构设计。

如果您之前直接引用了`chy-agents-image`模块，请更新为：

```xml
<dependency>
    <groupId>com.chy</groupId>
    <artifactId>chy-agents-multimodal</artifactId>
    <version>${project.version}</version>
</dependency>
```

或者直接引用子模块：

```xml
<dependency>
    <groupId>com.chy</groupId>
    <artifactId>chy-agents-multimodal-image</artifactId>
    <version>${project.version}</version>
</dependency>
```

## 配置说明

图像处理模块配置：

```yaml
chy:
  agents:
    multimodal:
      image:
        enabled: true
        default-provider: openai  # 可选：openai, stability, dashscope
        default-size: 1024x1024
        default-model: dall-e-3
```

## API使用示例

```java
@RestController
public class ImageDemoController {

    @Resource
    private ImageService imageService;
    
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateImage(@RequestBody GenerateImageRequest request) {
        return imageService.generateImage(request);
    }
}
``` 