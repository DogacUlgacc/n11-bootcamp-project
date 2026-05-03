package com.dogac.order_service.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateOrderItemRequest(
        @NotNull UUID productId,
        @Positive int quantity,
        @NotNull @PositiveOrZero BigDecimal unitPrice) {
}
