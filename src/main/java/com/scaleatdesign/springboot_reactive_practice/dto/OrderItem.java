package com.scaleatdesign.springboot_reactive_practice.dto;

import java.math.BigDecimal;

/**
 * A single line item within an order.
 */
public record OrderItem(
        String productId,
        String productName,
        int quantity,
        BigDecimal unitPrice
) {
    public BigDecimal totalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
