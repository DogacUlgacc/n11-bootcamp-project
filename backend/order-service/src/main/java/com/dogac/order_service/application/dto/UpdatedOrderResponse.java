package com.dogac.order_service.application.dto;

import java.util.UUID;

import com.dogac.order_service.domain.enums.OrderStatus;

public record UpdatedOrderResponse(UUID id, OrderStatus status) {
}
