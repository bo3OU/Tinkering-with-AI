package com.demo.updating_brain.geopost.shipping.dto;

public record OrderMetrics(
        Long orderId,
        int itemCount,
        double totalWeight,
        double maxLength,
        double maxWidth,
        double maxHeight,
        boolean hasFragileItems
) {}
