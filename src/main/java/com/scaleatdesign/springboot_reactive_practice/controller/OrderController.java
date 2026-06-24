package com.scaleatdesign.springboot_reactive_practice.controller;

import com.scaleatdesign.springboot_reactive_practice.dto.Order;
import com.scaleatdesign.springboot_reactive_practice.dto.OrderItem;
import com.scaleatdesign.springboot_reactive_practice.dto.OrderStatus;
import com.scaleatdesign.springboot_reactive_practice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Reactive REST controller for order management.
 *
 * Concepts demonstrated:
 * - flatMap for chaining dependent async calls
 * - Mono.zip for combining independent async results
 * - onErrorResume for reactive error handling
 * - Mono.error propagation and global exception handling
 * - ResponseEntity patterns for varied HTTP responses
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * GET /api/orders/{id}
     * Get a single order by ID.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Order>> getOrderById(@PathVariable String id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * GET /api/orders/customer/{customerId}
     * Get all orders for a customer.
     */
    @GetMapping("/customer/{customerId}")
    public Flux<Order> getOrdersByCustomer(@PathVariable String customerId) {
        return orderService.findByCustomerId(customerId);
    }

    /**
     * GET /api/orders/status/{status}
     * Get all orders with a given status.
     */
    @GetMapping("/status/{status}")
    public Flux<Order> getOrdersByStatus(@PathVariable OrderStatus status) {
        return orderService.findByStatus(status);
    }

    /**
     * POST /api/orders
     * Place a new order.
     * Demonstrates: flatMap chain (validate products → create order),
     *               onErrorResume for graceful error handling.
     *
     * Request body example:
     * {
     *   "customerId": "CUST-100",
     *   "items": [
     *     { "productId": "P001", "productName": "Wireless Mouse", "quantity": 2, "unitPrice": 29.99 }
     *   ]
     * }
     */
    @PostMapping
    public Mono<ResponseEntity<Object>> placeOrder(@RequestBody PlaceOrderRequest request) {
        return orderService.placeOrder(request.customerId(), request.items())
                .map(order -> ResponseEntity.status(HttpStatus.CREATED).body((Object) order))
                .onErrorResume(IllegalArgumentException.class, ex ->
                        Mono.just(ResponseEntity.badRequest().body(Map.of(
                                "error", ex.getMessage()
                        ))));
    }

    /**
     * PATCH /api/orders/{id}/status
     * Update order status.
     * Demonstrates: flatMap for dependent operations, switchIfEmpty for 404.
     *
     * Request body example: { "status": "SHIPPED" }
     */
    @PatchMapping("/{id}/status")
    public Mono<ResponseEntity<Order>> updateOrderStatus(
            @PathVariable String id,
            @RequestBody UpdateStatusRequest request) {
        return orderService.updateStatus(id, request.status())
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * GET /api/orders/{id}/enriched
     * Get order with latest product data (prices/names from catalog).
     * Demonstrates: flatMap + Flux for enrichment pattern (simulates a join).
     */
    @GetMapping("/{id}/enriched")
    public Mono<ResponseEntity<Order>> getEnrichedOrder(@PathVariable String id) {
        return orderService.getOrderWithProductValidation(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * GET /api/orders/revenue
     * Get total revenue across all orders.
     * Demonstrates: Flux.reduce for aggregation, returning a simple value.
     */
    @GetMapping("/revenue")
    public Mono<Map<String, BigDecimal>> getTotalRevenue() {
        return orderService.getTotalRevenue()
                .map(total -> Map.of("totalRevenue", total));
    }

    /**
     * Request DTO for placing an order.
     */
    record PlaceOrderRequest(String customerId, List<OrderItem> items) {}

    /**
     * Request DTO for updating order status.
     */
    record UpdateStatusRequest(OrderStatus status) {}
}
