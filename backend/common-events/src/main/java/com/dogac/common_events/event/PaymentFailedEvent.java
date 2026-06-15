package com.dogac.common_events.event;

import java.util.UUID;

public record PaymentFailedEvent(
                UUID eventId,
                UUID paymentId,
                UUID orderId,
                String reason) {
}