package com.demo.updating_brain.shipping;

import com.demo.updating_brain.entity.Item;
import com.demo.updating_brain.repository.ItemRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShippingMcpTools {

    private final ItemRepository itemRepository;
    private final ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;
    private final VectorStore shippingVectorStore;
    private ChatClient chatClient;

    public ShippingMcpTools(
            ItemRepository itemRepository,
            @Lazy ObjectProvider<ChatClient.Builder> chatClientBuilderProvider,
            @Qualifier("shippingVectorStore") VectorStore shippingVectorStore) {
        this.itemRepository = itemRepository;
        this.chatClientBuilderProvider = chatClientBuilderProvider;
        this.shippingVectorStore = shippingVectorStore;
    }

    private ChatClient getChatClient() {
        if (chatClient == null) {
            chatClient = chatClientBuilderProvider.getObject()
                    .defaultAdvisors(new QuestionAnswerAdvisor(shippingVectorStore))
                    .build();
        }
        return chatClient;
    }

    @Tool(description = "Get all items for an order by orderId. Returns the list of items with their dimensions, weights, and properties.")
    public List<Item> getItemsByOrderId(Long orderId) {
        return itemRepository.findByOrderId(orderId);
    }

    @Tool(description = "Find the best shipping box for a list of items. Analyzes item dimensions and weights to recommend the optimal container.")
    public String findBestBoxForItems(Long orderId) {
        List<Item> items = itemRepository.findByOrderId(orderId);

        if (items.isEmpty()) {
            return "No items found for order ID: " + orderId;
        }

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
            "I need to ship %d items for order ID %d with the following specifications:\n" +
            "- Total volume needed: %.2f cm³\n" +
            "- Total weight: %.2f kg\n" +
            "- Largest item dimensions: %.2f x %.2f x %.2f cm (L x W x H)\n" +
            "- Contains fragile items: %s\n\n" +
            "Based on the available shipping containers, recommend the MOST SUITABLE container. " +
            "Provide ONLY the container ID, name, dimensions, price, and a brief explanation. " +
            "Consider that items need some padding space (add 20%% to volume for safety).",
            items.size(),
            orderId,
            totalVolume,
            totalWeight,
            maxLength,
            maxWidth,
            maxHeight,
            hasFragile ? "Yes" : "No"
        );

        return getChatClient().prompt()
                .user(prompt)
                .call()
                .content();
    }
}
