package com.dogac.order_service.application.feignDto;

import java.util.List;
import java.util.UUID;

public record CartDto(
        UUID cartId,
        UUID userId,
        String currency,
        List<CartItemDto> items) {

}
