package com.chy.agents.alibaba.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContentSafetyFilter {

    @Autowired
    private GreenService greenService;

    public String filter(String content) {
        TextScanRequest request = new TextScanRequest(content);
        TextScanResponse response = greenService.textScan(request);
        
        if (response.hasRisk()) {
            throw new ContentSafetyException("内容安全风险: " + response.getRiskDetails());
        }
        return content;
    }
} 