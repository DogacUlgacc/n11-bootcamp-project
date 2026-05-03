package com.dogac.order_service.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(UUID productId, int quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
}
