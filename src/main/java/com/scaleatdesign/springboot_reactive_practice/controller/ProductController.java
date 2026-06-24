package com.scaleatdesign.springboot_reactive_practice.controller;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.scaleatdesign.springboot_reactive_practice.dto.Product;
import com.scaleatdesign.springboot_reactive_practice.service.ProductService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive REST controller for the product catalog.
 *
 * Concepts demonstrated:
 * - Returning Mono<T> for single-item responses
 * - Returning Flux<T> for collection/stream responses
 * - Mono<ResponseEntity<T>> for custom HTTP status codes
 * - switchIfEmpty for 404 handling
 * - Request params for filtering/search
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * GET /api/products
     * Returns all products as a reactive stream.
     */
    @GetMapping
    public Flux<Product> getAllProducts() {
        return productService.findAll();
    }

    /**
     * GET /api/products/{id}
     * Returns a single product, or 404 if not found.
     * Demonstrates: switchIfEmpty + Mono<ResponseEntity<T>> pattern.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> getProductById(@PathVariable String id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * GET /api/products/category/{category}
     * Filter products by category.
     */
    @GetMapping("/category/{category}")
    public Flux<Product> getProductsByCategory(@PathVariable String category) {
        return productService.findByCategory(category);
    }

    /**
     * GET /api/products/search?keyword=mouse
     * Search products by name keyword.
     */
    @GetMapping("/search")
    public Flux<Product> searchProducts(@RequestParam String keyword) {
        return productService.searchByName(keyword);
    }

    /**
     * GET /api/products/price?min=20&max=100
     * Find products within a price range.
     */
    @GetMapping("/price")
    public Flux<Product> getProductsByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return productService.findByPriceRange(min, max);
    }

    /**
     * POST /api/products
     * Create a new product.
     * Demonstrates: @RequestBody with Mono, @ResponseStatus for 201.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Product> createProduct(@RequestBody Product product) {
        return productService.save(product);
    }

    /**
     * DELETE /api/products/{id}
     * Delete a product. Returns 204 if deleted, 404 if not found.
     * Demonstrates: map + switchIfEmpty for conditional status codes.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable String id) {
        return productService.deleteById(id)
                .map(deleted -> ResponseEntity.noContent().<Void>build())
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
