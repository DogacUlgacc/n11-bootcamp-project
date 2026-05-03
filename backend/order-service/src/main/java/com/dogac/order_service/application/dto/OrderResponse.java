package com.dogac.order_service.application.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.dogac.order_service.domain.enums.OrderStatus;

public record OrderResponse(UUID id, String orderNumber, UUID userId, List<OrderItemResponse> items, BigDecimal totalAmount,
        OrderStatus status) {
}
