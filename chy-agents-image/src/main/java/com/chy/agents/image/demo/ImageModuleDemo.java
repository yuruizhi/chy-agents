package com.chy.agents.image.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 图像模块演示应用
 * 
 * 这是一个独立的Spring Boot应用，用于演示图像生成和分析功能
 * 运行后，可以通过以下URL访问：
 * - 图像生成：http://localhost:8080/examples/image/generate/simple?prompt=你的提示文本
 * - 图像分析：http://localhost:8080/examples/image/analyze (POST请求，上传图片)
 * - 生成并分析：http://localhost:8080/examples/image/generate-and-analyze?generationPrompt=生成提示&analysisPrompt=分析提示
 */
@SpringBootApplication
@ComponentScan("com.chy.agents.image")
public class ImageModuleDemo {

    public static void main(String[] args) {
        // 设置默认的API键（仅用于演示，实际应用中应使用环境变量或配置文件）
        if (System.getProperty("spring.ai.stability-ai.api-key") == null) {
            System.setProperty("spring.ai.stability-ai.api-key", "YOUR_STABILITY_API_KEY");
        }
        
        if (System.getProperty("spring.ai.openai.api-key") == null) {
            System.setProperty("spring.ai.openai.api-key", "YOUR_OPENAI_API_KEY");
        }
        
        SpringApplication.run(ImageModuleDemo.class, args);
        
        System.out.println("\n=====================================================");
        System.out.println("图像模块演示应用已启动！");
        System.out.println("请访问以下URL体验功能：");
        System.out.println("1. 图像生成：http://localhost:8080/examples/image/generate/simple?prompt=一只可爱的猫咪");
        System.out.println("2. 图像分析：http://localhost:8080/examples/image/analyze (POST请求，上传图片)");
        System.out.println("3. 生成并分析：http://localhost:8080/examples/image/generate-and-analyze?generationPrompt=未来城市&analysisPrompt=描述这个城市");
        System.out.println("=====================================================\n");
    }
} 