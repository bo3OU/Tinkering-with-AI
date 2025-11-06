package com.demo.updating_brain.shipping;

import com.demo.updating_brain.entity.Item;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipping")
@Tag(name = "Shipping RAG Controller", description = "AI-powered shipping container recommendations using RAG")
public class ShippingRagController {

    private final ChatClient chatClient;
    private final ShippingMcpTools shippingMcpTools;

    public ShippingRagController(
            ChatClient.Builder builder,
            @Qualifier("shippingVectorStore") VectorStore shippingVectorStore,
            ShippingMcpTools shippingMcpTools) {
        this.chatClient = builder
                .defaultAdvisors(new QuestionAnswerAdvisor(shippingVectorStore))
                .build();
        this.shippingMcpTools = shippingMcpTools;
    }

    @GetMapping("/query-containers")
    @Operation(summary = "Query shipping containers",
               description = "Query the vector store to get information about available shipping containers")
    public String queryContainers(@RequestParam("query") String query) {
        return chatClient.prompt()
                .tools(shippingMcpTools)
                .tools()
                .user(query)
                .call()
                .content();
    }

    @PostMapping("/find-best-box")
    @Operation(summary = "Find best box for items",
               description = "Calculate the best shipping container for a list of items based on their dimensions and weight")
    public String findBestBox(@RequestBody List<Item> items) {
        // Calculate total volume and weight
        double totalVolume = 0;
        double totalWeight = 0;
        double maxLength = 0;
        double maxWidth = 0;
        double maxHeight = 0;
        boolean hasFragile = false;

        for (Item item : items) {
            if (item.getLength() != null && item.getWidth() != null && item.getHeight() != null) {
                double itemVolume = item.getLength() * item.getWidth() * item.getHeight();
                totalVolume += itemVolume;

                maxLength = Math.max(maxLength, item.getLength());
                maxWidth = Math.max(maxWidth, item.getWidth());
                maxHeight = Math.max(maxHeight, item.getHeight());
            }

            if (item.getWeightKg() != null) {
                totalWeight += item.getWeightKg();
            }

            if (item.getFragile() != null && item.getFragile()) {
                hasFragile = true;
            }
        }

        // Build the prompt for AI
        String prompt = String.format(
            "I need to ship %d items with the following specifications:\n" +
            "- Total volume needed: %.2f cm³\n" +
            "- Total weight: %.2f kg\n" +
            "- Largest item dimensions: %.2f x %.2f x %.2f cm (L x W x H)\n" +
            "- Contains fragile items: %s\n\n" +
            "Based on the available shipping containers, recommend the MOST SUITABLE container. " +
            "Provide the container ID, name, dimensions, and explain why it's the best choice. " +
            "Consider that items need some padding space (add 20%% to volume for safety).",
            items.size(),
            totalVolume,
            totalWeight,
            maxLength,
            maxWidth,
            maxHeight,
            hasFragile ? "Yes" : "No"
        );

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
