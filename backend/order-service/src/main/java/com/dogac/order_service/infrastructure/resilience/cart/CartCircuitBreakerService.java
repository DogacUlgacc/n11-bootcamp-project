package com.dogac.order_service.infrastructure.resilience.cart;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dogac.order_service.application.feignDto.CartDto;
import com.dogac.order_service.infrastructure.feignclients.CartClient;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class CartCircuitBreakerService {

    private final CartClient cartClient;

    
    public CartCircuitBreakerService(CartClient cartClient) {
        this.cartClient = cartClient;
    }

    @CircuitBreaker(name = "cartService", fallbackMethod = "getCartByIdFallback")
    public CartDto getCartById(UUID id) {

        return cartClient.getCartById(id);
    }

    public CartDto getCartByIdFallback(
            UUID id,
            Throwable throwable) {

        if (throwable instanceof CallNotPermittedException) {
            throw new RuntimeException(
                    "User service is currently unavailable",
                    throwable);
        }

        sneakyThrow(throwable);
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void sneakyThrow(
            Throwable throwable) throws T {

        throw (T) throwable;
    }
}
