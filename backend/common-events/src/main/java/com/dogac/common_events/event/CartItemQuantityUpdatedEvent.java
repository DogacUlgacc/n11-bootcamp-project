package com.dogac.common_events.event;

import java.util.UUID;

public record CartItemQuantityUpdatedEvent(
        UUID cartId,
        UUID productId,
        int oldQuantity,
        int newQuantity,
        int delta) {
}