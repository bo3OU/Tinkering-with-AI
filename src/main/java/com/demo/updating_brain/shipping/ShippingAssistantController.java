package com.demo.updating_brain.shipping;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shipping")
@Tag(name = "Shipping RAG Controller", description = "AI-powered shipping container recommendations using RAG")
public class ShippingAssistantController {

    private final ChatClient chatClient;
    private final ShippingMcpTools shippingMcpTools;

    public ShippingAssistantController(
            ChatClient.Builder builder,
            @Qualifier("shippingVectorStore") VectorStore shippingVectorStore,
            ShippingMcpTools shippingMcpTools) {
        this.chatClient = builder
                .defaultAdvisors(new QuestionAnswerAdvisor(shippingVectorStore))
                .build();
        this.shippingMcpTools = shippingMcpTools;
    }

    @GetMapping("/order-status")
    public ContainerList queryContainers(@RequestParam("orderId") Integer orderId, @RequestParam("withMessage") Boolean withMessage) {
        return chatClient.prompt()
                .tools(shippingMcpTools)
                .system(u -> {
                    u.text("""
                            You are a shipping container recommendation system.
                            You have access to a database of shipping containers via RAG (vector search - shippingVectorStore).
                            
                            Follow these steps:
                            the orderId is {orderId} and was sent by the user
                            If the user sent a false orderId, return an empty container list, not null.
                            
                            IMPORTANT: When returning structured data, return ONLY raw JSON without any markdown formatting,
                            code blocks, or backticks. Do not wrap the response in ```json or ```.
                            """);
                    u.param("orderId", orderId);
                })
                .user(u -> {
                    u.text("""
                            
                            Task: Find suitable shipping containers for list of items.
                            Step 1: Find the Items of the order,
                            Step 2: check each box if it can contain the list of items
                            step 3: keep only those who can hold the list of items.
                            step 4: order by price, cheapest first.
                            """);
                    u.param("orderId", orderId);
                    if (withMessage) {
                        u.text("""
                                IMPORTANT: Also call the send_telegram_message tool to notify the user.
                                
                                Follow these steps:
                                1. Get the user by orderId
                                2. Get the order details (status, locationCity, destination, estimationDate)
                                3. Format the message EXACTLY as shown below while REPLACING VALUES IN BRACKETS:
                                
                                📦 Order #[orderId] - Shipping Update
                                
                                📍 Current position: [locationCity]
                                🎯 Destination: [destination]
                                🔖 Status: [status]
                                📅 Estimated Delivery: [estimationDate]
                                
                                📋 Package Contents ([N] items):
                                • [Item name]
                                • [Item name]
                                
                                4. IF any item's dimensions exceed 34×26×26 cm, add this line at the end:
                                
                                 `The package will not fit in the mailing box, we will call you when it arrives`
                                
                                5. Send this formatted message to the user's Telegram channel
                                """);
                    }
                })
                .call()
                .entity(ContainerList.class);
    }
}
