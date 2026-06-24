package com.scaleatdesign.springboot_reactive_practice.service;

import com.scaleatdesign.springboot_reactive_practice.dto.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reactive order service backed by an in-memory store.
 *
 * Key reactive concepts demonstrated:
 * - flatMap for chaining async operations (validate product → create order)
 * - Mono.zip for combining multiple independent async results
 * - Mono.error for reactive error propagation
 * - collectList to gather Flux elements into a Mono<List>
 * - reduce for aggregating stream elements
 */
@Service
public class OrderService {

    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    private final ProductService productService;

    public OrderService(ProductService productService) {
        this.productService = productService;

        // Seed a couple of sample orders
        Order sampleOrder = new Order(
                "ORD-001",
                "CUST-100",
                List.of(
                        new OrderItem("P001", "Wireless Mouse", 2, new BigDecimal("29.99")),
                        new OrderItem("P002", "Mechanical Keyboard", 1, new BigDecimal("89.99"))
                ),
                OrderStatus.CONFIRMED,
                LocalDateTime.now().minusDays(2),
                new BigDecimal("149.97")
        );

        Order sampleOrder2 = new Order(
                "ORD-002",
                "CUST-101",
                List.of(
                        new OrderItem("P004", "Standing Desk", 1, new BigDecimal("399.99"))
                ),
                OrderStatus.SHIPPED,
                LocalDateTime.now().minusDays(1),
                new BigDecimal("399.99")
        );

        orders.put(sampleOrder.id(), sampleOrder);
        orders.put(sampleOrder2.id(), sampleOrder2);
    }

    /**
     * Find an order by ID.
     */
    public Mono<Order> findById(String id) {
        return Mono.justOrEmpty(orders.get(id));
    }

    /**
     * Find all orders for a given customer.
     * Demonstrates: Flux filter.
     */
    public Flux<Order> findByCustomerId(String customerId) {
        return Flux.fromIterable(orders.values())
                .filter(order -> order.customerId().equals(customerId));
    }

    /**
     * Find all orders with a specific status.
     */
    public Flux<Order> findByStatus(OrderStatus status) {
        return Flux.fromIterable(orders.values())
                .filter(order -> order.status() == status);
    }

    /**
     * Place a new order.
     * Demonstrates:
     * - flatMap to chain: validate each product exists → build order
     * - Flux.fromIterable + flatMap for validating multiple items in parallel
     * - collectList to wait for all validations
     * - Mono.error for reactive error signaling
     */
    public Mono<Order> placeOrder(String customerId, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Order must contain at least one item"));
        }

        // Validate all products exist and build the order
        return Flux.fromIterable(items)
                .flatMap(item -> productService.findById(item.productId())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException(
                                "Product not found: " + item.productId())))
                        .map(product -> item))
                .collectList()
                .map(validatedItems -> {
                    BigDecimal total = validatedItems.stream()
                            .map(OrderItem::totalPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    Order order = new Order(
                            "ORD-" + UUID.randomUUID().toString().substring(0, 8),
                            customerId,
                            validatedItems,
                            OrderStatus.CREATED,
                            LocalDateTime.now(),
                            total
                    );

                    orders.put(order.id(), order);
                    return order;
                });
    }

    /**
     * Update order status.
     * Demonstrates: flatMap for read-modify-write pattern with Mono.
     */
    public Mono<Order> updateStatus(String orderId, OrderStatus newStatus) {
        return findById(orderId)
                .flatMap(existingOrder -> {
                    Order updated = new Order(
                            existingOrder.id(),
                            existingOrder.customerId(),
                            existingOrder.items(),
                            newStatus,
                            existingOrder.createdAt(),
                            existingOrder.totalAmount()
                    );
                    orders.put(updated.id(), updated);
                    return Mono.just(updated);
                });
    }

    /**
     * Get total revenue across all orders.
     * Demonstrates: Flux.reduce for aggregation.
     */
    public Mono<BigDecimal> getTotalRevenue() {
        return Flux.fromIterable(orders.values())
                .map(Order::totalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get order with enriched product details (simulate joining data).
     * Demonstrates: Mono.zip to combine order + product lookups in parallel.
     */
    public Mono<Order> getOrderWithProductValidation(String orderId) {
        return findById(orderId)
                .flatMap(order -> Flux.fromIterable(order.items())
                        .flatMap(item -> productService.findById(item.productId())
                                .map(product -> new OrderItem(
                                        product.id(),
                                        product.name(),
                                        item.quantity(),
                                        product.price()
                                )))
                        .collectList()
                        .map(enrichedItems -> new Order(
                                order.id(),
                                order.customerId(),
                                enrichedItems,
                                order.status(),
                                order.createdAt(),
                                order.totalAmount()
                        )));
    }
}
