package com.chy.agents.common.prompt;

import org.springframework.stereotype.Component;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 模板管理器。
 *
 * @author YuRuizhi
 * @date 2025/3/12
 */
@Component
public class PromptTemplateManager {
    
    private final Map<String, PromptTemplate> templates = new HashMap<>();
    
    public void registerTemplate(String name, PromptTemplate template) {
        templates.put(name, template);
    }
    
    public PromptTemplate getTemplate(String name) {
        return templates.get(name);
    }
    
    public String format(String templateName, Map<String, Object> parameters) {
        PromptTemplate template = getTemplate(templateName);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateName);
        }
        return template.render(parameters);
    }
} 