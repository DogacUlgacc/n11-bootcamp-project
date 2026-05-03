package com.dogac.payment_service.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "iyzico")
public record IyzicoProperties(String apiKey,
        String secretKey,
        String baseUrl) {
}
