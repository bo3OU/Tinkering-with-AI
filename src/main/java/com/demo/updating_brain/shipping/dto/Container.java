package com.demo.updating_brain.shipping.dto;

public record Container(
        String id,
        String name,
        String type,
        double length,
        double width,
        double height,
        String unit,
        double maxWeightKg,
        double volumeCm3,
        String material,
        double price,
        String idealFor
) {}
