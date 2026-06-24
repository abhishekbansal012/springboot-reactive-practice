package com.scaleatdesign.springboot_reactive_practice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a customer order containing one or more items.
 */
public record Order(
        String id,
        String customerId,
        List<OrderItem> items,
        OrderStatus status,
        LocalDateTime createdAt,
        BigDecimal totalAmount
) {
}
