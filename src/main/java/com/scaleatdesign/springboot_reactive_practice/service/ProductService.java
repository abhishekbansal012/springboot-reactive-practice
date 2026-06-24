package com.scaleatdesign.springboot_reactive_practice.service;

import com.scaleatdesign.springboot_reactive_practice.dto.Product;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reactive product service backed by an in-memory store.
 *
 * Key reactive concepts demonstrated:
 * - Mono.justOrEmpty / Mono.empty for optional lookups
 * - Flux.fromIterable for converting collections to reactive streams
 * - Mono.defer for lazy evaluation
 * - switchIfEmpty for fallback when no data found
 */
@Service
public class ProductService {

    private final Map<String, Product> products = new ConcurrentHashMap<>();

    public ProductService() {
        // Seed some sample products
        products.put("P001", new Product("P001", "Wireless Mouse", "Ergonomic wireless mouse with USB receiver", new BigDecimal("29.99"), "Electronics", 150));
        products.put("P002", new Product("P002", "Mechanical Keyboard", "RGB mechanical keyboard with Cherry MX switches", new BigDecimal("89.99"), "Electronics", 75));
        products.put("P003", new Product("P003", "USB-C Hub", "7-in-1 USB-C hub with HDMI and ethernet", new BigDecimal("49.99"), "Electronics", 200));
        products.put("P004", new Product("P004", "Standing Desk", "Adjustable electric standing desk 60x30 inches", new BigDecimal("399.99"), "Furniture", 30));
        products.put("P005", new Product("P005", "Monitor Arm", "Dual monitor arm with cable management", new BigDecimal("79.99"), "Furniture", 60));
        products.put("P006", new Product("P006", "Laptop Backpack", "Water-resistant laptop backpack with USB port", new BigDecimal("44.99"), "Accessories", 120));
    }

    /**
     * Find a single product by ID.
     * Returns Mono.empty() if not found (instead of throwing).
     */
    public Mono<Product> findById(String id) {
        return Mono.justOrEmpty(products.get(id));
    }

    /**
     * Return all products as a Flux stream.
     */
    public Flux<Product> findAll() {
        return Flux.fromIterable(products.values());
    }

    /**
     * Filter products by category.
     * Demonstrates: Flux filtering with a predicate.
     */
    public Flux<Product> findByCategory(String category) {
        return Flux.fromIterable(products.values())
                .filter(product -> product.category().equalsIgnoreCase(category));
    }

    /**
     * Search products by name (case-insensitive contains).
     * Demonstrates: Flux filter with string operations.
     */
    public Flux<Product> searchByName(String keyword) {
        return Flux.fromIterable(products.values())
                .filter(product -> product.name().toLowerCase().contains(keyword.toLowerCase()));
    }

    /**
     * Find products within a price range.
     * Demonstrates: Flux filter with multiple conditions.
     */
    public Flux<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return Flux.fromIterable(products.values())
                .filter(product -> product.price().compareTo(minPrice) >= 0
                        && product.price().compareTo(maxPrice) <= 0);
    }

    /**
     * Save or update a product.
     * Demonstrates: Mono.defer for lazy side-effect execution.
     */
    public Mono<Product> save(Product product) {
        return Mono.defer(() -> {
            products.put(product.id(), product);
            return Mono.just(product);
        });
    }

    /**
     * Delete a product by ID. Returns the deleted product if it existed.
     * Demonstrates: Mono.justOrEmpty with a mutating operation.
     */
    public Mono<Product> deleteById(String id) {
        return Mono.justOrEmpty(products.remove(id));
    }
}
