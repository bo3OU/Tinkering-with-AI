package com.demo.updating_brain.geopost.shipping.service;

import com.demo.updating_brain.geopost.shipping.entity.Item;
import com.demo.updating_brain.geopost.shipping.entity.Order;
import com.demo.updating_brain.geopost.shipping.entity.User;
import com.demo.updating_brain.geopost.shipping.repository.ItemRepository;
import com.demo.updating_brain.geopost.shipping.repository.OrderRepository;
import com.demo.updating_brain.geopost.shipping.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class ShippingMcpTools {

    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestClient restClient;

    @Value("${telegram.bot.token:}")
    private String telegramBotToken;

    public ShippingMcpTools(ItemRepository itemRepository,
                            OrderRepository orderRepository,
                            UserRepository userRepository,
                            ObjectMapper objectMapper) {
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.restClient = RestClient.create();
    }

    @Tool(name = "get_items_by_order_id", description = "Get all items for an order by orderId. Returns the list of items with their dimensions, weights, and properties.")
    public List<Item> getItemsByOrderId(Long orderId) {
        return itemRepository.findByOrderId(orderId);
    }

//    @Tool(name = "calculate_order_shipping_metrics", description = "Calculate shipping metrics for a list of items. Returns the smallest container dimensions needed, total weight, and fragile status.")
//    public OrderMetrics calculateOrderMetrics(Long orderId, List<Item> items) {
//        if (items.isEmpty()) {
//            return new OrderMetrics(orderId, 0, 0.0, 0.0, 0.0, 0.0, false);
//        }
//
//        double totalWeight = 0;
//        double maxLength = 0;
//        double maxWidth = 0;
//        double maxHeight = 0;
//        boolean hasFragile = false;
//
//        for (Item item : items) {
//            if (item.getLength() != null && item.getWidth() != null && item.getHeight() != null) {
//                maxLength = Math.max(maxLength, item.getLength());
//                maxWidth = Math.max(maxWidth, item.getWidth());
//                maxHeight = Math.max(maxHeight, item.getHeight());
//            }
//
//            if (item.getWeightKg() != null) {
//                totalWeight += item.getWeightKg();
//            }
//
//            if (item.getFragile() != null && item.getFragile()) {
//                hasFragile = true;
//            }
//        }
//
//        return new OrderMetrics(orderId, items.size(), totalWeight, maxLength, maxWidth, maxHeight, hasFragile);
//    }

    @Tool(name = "get_order_by_id", description = "Get order details by orderId. Returns the Order object with status, locationCity, destination, and estimationDate.")
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Tool(name = "get_user_by_order_id", description = "Find the user associated with an order by orderId. Returns the User object with their details.")
    public User getUserByOrderId(Long orderId) {
        return userRepository.findByOrderId(orderId);
    }

    @Tool(name = "send_telegram_message", description = "Send a message to a user's Telegram channel. Provide the Telegram channel (e.g., @username) and the message text.")
    public String sendTelegramMessage(String telegramChannel, String message) {
        if (telegramBotToken == null || telegramBotToken.isEmpty()) {
            return "Telegram bot token not configured. Please set telegram.bot.token in application.properties";
        }

        try {
            String url = String.format("https://api.telegram.org/bot%s/sendMessage", telegramBotToken);

            Map<String, Object> requestBody = Map.of(
                "chat_id", telegramChannel,
                "text", message,
                "parse_mode", "Markdown"
            );

            Map<String, Object> response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (response != null && Boolean.TRUE.equals(response.get("ok"))) {
                return "Message sent successfully to " + telegramChannel;
            } else {
                return "Failed to send message: " + response;
            }
        } catch (Exception e) {
            return "Error sending Telegram message: " + e.getMessage();
        }
    }
}
