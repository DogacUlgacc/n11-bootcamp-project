package com.dogac.order_service.application.dto;

import com.dogac.order_service.domain.enums.OrderStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateOrderRequest(@NotNull OrderStatus status) {
}
