package com.dogac.common_events.event;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedEvent(
                UUID orderId,
                UUID userId,
                UUID cartId,
                BigDecimal totalAmount,
                String currency) {
}