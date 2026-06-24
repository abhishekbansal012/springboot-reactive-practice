package com.scaleatdesign.springboot_reactive_practice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient configuration for making external API calls.
 * No base URL set — individual services will specify their target URLs.
 * This keeps the client reusable across different external integrations
 * (e.g., payment gateway, shipping provider, inventory service).
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}
