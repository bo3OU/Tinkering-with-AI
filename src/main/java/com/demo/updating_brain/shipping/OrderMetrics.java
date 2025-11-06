package com.demo.updating_brain.shipping;

public record OrderMetrics(
        Long orderId,
        int itemCount,
        double totalWeight,
        double maxLength,
        double maxWidth,
        double maxHeight,
        boolean hasFragileItems
) {}
