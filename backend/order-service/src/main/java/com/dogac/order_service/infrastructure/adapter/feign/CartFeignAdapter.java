package com.dogac.order_service.infrastructure.adapter.feign;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dogac.order_service.application.feignDto.CartDto;
import com.dogac.order_service.application.port.CartPort;
import com.dogac.order_service.infrastructure.resilience.cart.CartRetryService;

@Component
public class CartFeignAdapter implements CartPort {

    private final CartRetryService cartRetryService;

    public CartFeignAdapter(CartRetryService cartRetryService) {
        this.cartRetryService = cartRetryService;
    }

    @Override
    public CartDto getCartById(UUID id) {
        return cartRetryService.getCartById(id);
    }

}
