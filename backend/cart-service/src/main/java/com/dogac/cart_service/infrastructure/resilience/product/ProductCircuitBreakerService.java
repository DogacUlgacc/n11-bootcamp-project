package com.dogac.cart_service.infrastructure.resilience.product;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dogac.cart_service.application.dto.feignDto.ProductDto;
import com.dogac.cart_service.infrastructure.feignclient.ProductClient;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class ProductCircuitBreakerService {

    private final ProductClient productClient;

    public ProductCircuitBreakerService(ProductClient productClient) {
        this.productClient = productClient;
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "getProductByIdFallback")
    public ProductDto getProductById(UUID id) {
        return productClient.getProductById(id);
    }

    public ProductDto getProductByIdFallback(
            UUID id,
            Throwable throwable) {

        if (throwable instanceof CallNotPermittedException callNotPermittedException) {
            throw new RuntimeException(
                    "Product service is currently unavailable",
                    callNotPermittedException);
        }

        sneakyThrow(throwable);
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void sneakyThrow(Throwable throwable) throws T {
        throw (T) throwable;
    }
}