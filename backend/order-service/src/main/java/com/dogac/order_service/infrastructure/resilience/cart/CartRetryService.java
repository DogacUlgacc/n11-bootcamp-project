package com.dogac.order_service.infrastructure.resilience.cart;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dogac.order_service.application.feignDto.CartDto;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CartRetryService {

    private final CartCircuitBreakerService cartCircuitBreakerService;

    public CartRetryService(CartCircuitBreakerService cartCircuitBreakerService) {
        this.cartCircuitBreakerService = cartCircuitBreakerService;
    }

    @Retry(name = "cartService")
    public CartDto getCartById(UUID id) {
        return cartCircuitBreakerService.getCartById(id);
    }
}
