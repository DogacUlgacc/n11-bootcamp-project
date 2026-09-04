package com.dogac.cart_service.infrastructure.resilience.product;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dogac.cart_service.application.dto.feignDto.ProductDto;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductRetryService {

    private final ProductCircuitBreakerService productCircuitBreakerService;

    public ProductRetryService(ProductCircuitBreakerService productCircuitBreakerService) {
        this.productCircuitBreakerService = productCircuitBreakerService;
    }

    @Retry(name = "productService")
    public ProductDto getProductById(UUID id) {
        log.info("Calling Product Service: {}", id);
        return productCircuitBreakerService.getProductById(id);
    }
}