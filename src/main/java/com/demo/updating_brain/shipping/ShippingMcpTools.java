package com.demo.updating_brain.shipping;

import com.demo.updating_brain.entity.Item;
import com.demo.updating_brain.entity.Order;
import com.demo.updating_brain.entity.User;
import com.demo.updating_brain.repository.ItemRepository;
import com.demo.updating_brain.repository.OrderRepository;
import com.demo.updating_brain.repository.UserRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class ShippingMcpTools {

    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${telegram.bot.token:}")
    private String telegramBotToken;

    public ShippingMcpTools(ItemRepository itemRepository,
                            OrderRepository orderRepository,
                            UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.restTemplate = new RestTemplate();
    }

    @Tool(name = "get_items_by_order_id", description = "Get all items for an order by orderId. Returns the list of items with their dimensions, weights, and properties.")
    public List<Item> getItemsByOrderId(Long orderId) {
        return itemRepository.findByOrderId(orderId);
    }

    @Tool(name = "calculate_order_shipping_metrics", description = "Calculate shipping metrics for an order. Returns total volume, weight, max dimensions, and fragile status.")
    public OrderMetrics calculateOrderMetrics(Long orderId) {
        List<Item> items = itemRepository.findByOrderId(orderId);

        if (items.isEmpty()) {
            return new OrderMetrics(orderId, 0, 0.0, 0.0, 0.0, 0.0, 0.0, false);
        }

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

        return new OrderMetrics(orderId, items.size(), totalVolume, totalWeight,
                                maxLength, maxWidth, maxHeight, hasFragile);
    }

    public record OrderMetrics(
            Long orderId,
            int itemCount,
            double totalVolumeCm3,
            double totalWeightKg,
            double maxLengthCm,
            double maxWidthCm,
            double maxHeightCm,
            boolean hasFragileItems
    ) {}

    @Tool(name = "get_telegram_channel_by_order_id", description = "Get the user's Telegram channel associated with an order. Returns the Telegram channel username.")
    public String getTelegramChannelByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElse(null);

        if (order == null) {
            return "Order not found with ID: " + orderId;
        }

        User user = userRepository.findById(order.getUserId())
                .orElse(null);

        if (user == null) {
            return "User not found for order ID: " + orderId;
        }

        return user.getTelegramChannel();
    }

    @Tool(name = "send_telegram_message", description = "Send a message to a user's Telegram channel. Provide the Telegram channel (e.g., @username) and the message text.")
    public String sendTelegramMessage(String telegramChannel, String message) {
        if (telegramBotToken == null || telegramBotToken.isEmpty()) {
            return "Telegram bot token not configured. Please set telegram.bot.token in application.properties";
        }

        try {
            String url = String.format("https://api.telegram.org/bot%s/sendMessage", telegramBotToken);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = Map.of(
                "chat_id", telegramChannel,
                "text", message,
                "parse_mode", "Markdown"
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

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
