package com.demo.updating_brain.shipping;

import com.demo.updating_brain.entity.Item;
import com.demo.updating_brain.repository.ItemRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShippingMcpTools {

    private final ItemRepository itemRepository;

    public ShippingMcpTools(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
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
}
