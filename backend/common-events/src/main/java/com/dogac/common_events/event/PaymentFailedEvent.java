package com.dogac.common_events.event;

import java.util.UUID;

public record PaymentFailedEvent(
        UUID paymentId,
        UUID orderId,
        String reason) {
}