package com.scaleatdesign.springboot_reactive_practice.dto;

import java.math.BigDecimal;

/**
 * Represents a product in the catalog.
 */
public record Product(
        String id,
        String name,
        String description,
        BigDecimal price,
        String category,
        int stockQuantity
) {
}
