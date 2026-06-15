package com.dogac.common_events.event;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentSucceededEvent(
        UUID eventId,
        UUID paymentId,
        UUID orderId,
        UUID userId,
        BigDecimal amount,
        String currency) {
}