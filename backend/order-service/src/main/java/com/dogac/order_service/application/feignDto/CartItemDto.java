package com.dogac.order_service.application.feignDto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemDto(
        UUID id,
        UUID productId,
        Integer quantity,
        BigDecimal unitPrice,
        String currency) {

}
