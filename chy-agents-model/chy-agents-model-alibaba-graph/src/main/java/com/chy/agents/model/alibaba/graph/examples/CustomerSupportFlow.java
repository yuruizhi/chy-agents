package com.chy.agents.model.alibaba.graph.examples;

import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.StateGraphFactory;
import com.alibaba.cloud.ai.graph.Workflow;
import com.alibaba.cloud.ai.graph.WorkflowExecutor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户支持多智能体工作流示例
 * 
 * @author YuRuizhi
 * @date 2025/06/19
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.alibaba.graph", name = "customer-support-demo", havingValue = "true", matchIfMissing = false)
public class CustomerSupportFlow {

    @Autowired
    private StateGraphFactory stateGraphFactory;
    
    @Autowired
    private WorkflowExecutor workflowExecutor;
    
    @Autowired
    @Qualifier("researchChatClient")
    private ChatClient researchChatClient;
    
    @Autowired
    @Qualifier("summaryChatClient")
    private ChatClient summaryChatClient;
    
    /**
     * 创建客户支持工作流
     * 
     * @return 状态图实例
     */
    @Bean
    public StateGraph customerSupportGraph() {
        return new StateGraph("Customer Support Flow", stateGraphFactory)
            // 分类节点：对客户查询进行分类
            .addNode("classifier", StateGraphFactory.llmNode(researchChatClient,
                """
                你是一个客户支持分类器。根据客户问题，将查询分为以下类别之一：
                - 技术问题
                - 账单问题
                - 产品咨询
                - 投诉
                仅返回类别名称，不要添加额外解释。
                """))
            
            // 技术支持节点
            .addNode("tech_support", StateGraphFactory.llmNode(researchChatClient,
                """
                你是一位技术支持专员。提供清晰、准确的技术问题解答。
                包括故障排除步骤、常见问题解决方案和技术指导。
                回答应专业但通俗易懂。
                """))
            
            // 账单支持节点
            .addNode("billing_support", StateGraphFactory.llmNode(researchChatClient,
                """
                你是一位账单支持专员。解答有关账单、付款、订阅和退款的问题。
                提供清晰的步骤和政策说明，确保客户了解财务流程。
                回答应专业且有帮助。
                """))
            
            // 产品咨询节点
            .addNode("product_info", StateGraphFactory.llmNode(researchChatClient,
                """
                你是一位产品专家。提供有关产品功能、规格、兼容性和使用建议的详细信息。
                帮助客户选择最适合其需求的产品，并回答产品相关问题。
                回答应信息丰富且有说服力。
                """))
            
            // 投诉处理节点
            .addNode("complaint_handler", StateGraphFactory.llmNode(researchChatClient,
                """
                你是一位客户关系专员。处理客户投诉时应表示同理心，承认问题，
                并提供解决方案。确保客户感到被重视和尊重。
                回答应富有同情心且面向解决方案。
                """))
            
            // 回复格式化节点
            .addNode("response_formatter", StateGraphFactory.llmNode(summaryChatClient,
                """
                你是回复格式化专家。根据专家提供的回答，创建一个礼貌、专业的客户回复。
                包含以下部分：
                1. 个性化问候
                2. 对问题的理解确认
                3. 详细解答
                4. 跟进建议
                5. 礼貌结束语
                保持语言友好、清晰且易于理解。
                """))
            
            // 定义工作流边
            .addEdge("classifier", "tech_support", (input, output) -> "技术问题".equals(output))
            .addEdge("classifier", "billing_support", (input, output) -> "账单问题".equals(output))
            .addEdge("classifier", "product_info", (input, output) -> "产品咨询".equals(output))
            .addEdge("classifier", "complaint_handler", (input, output) -> "投诉".equals(output))
            .addEdge("tech_support", "response_formatter")
            .addEdge("billing_support", "response_formatter")
            .addEdge("product_info", "response_formatter")
            .addEdge("complaint_handler", "response_formatter");
    }
    
    /**
     * 演示运行器
     */
    @Bean
    public CommandLineRunner demoRunner() {
        return args -> {
            System.out.println("开始客户支持多智能体演示...");
            
            // 示例客户查询
            String customerQuery = "我的月度订阅被多收费了，上个月被收取了两次费用，请帮我解决。";
            
            // 准备上下文
            Map<String, Object> context = new HashMap<>();
            context.put("input", customerQuery);
            context.put("customerName", "张先生");
            context.put("accountId", "AC1234567");
            
            // 编译并执行工作流
            Workflow workflow = customerSupportGraph().compile();
            Map<String, Object> result = workflowExecutor.execute(workflow, context).get();
            
            // 输出结果
            System.out.println("====== 客户查询 ======");
            System.out.println(customerQuery);
            System.out.println("\n====== 系统回复 ======");
            System.out.println(result.get("response_formatter"));
            System.out.println("\n演示完成。");
        };
    }
}