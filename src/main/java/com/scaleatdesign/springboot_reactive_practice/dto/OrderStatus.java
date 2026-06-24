package com.scaleatdesign.springboot_reactive_practice.dto;

/**
 * Represents the lifecycle states of an order.
 */
public enum OrderStatus {
    CREATED,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
