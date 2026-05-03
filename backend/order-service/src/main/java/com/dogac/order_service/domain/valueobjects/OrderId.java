package com.dogac.order_service.domain.valueobjects;

import java.util.UUID;

public record OrderId(UUID value) {
    public static OrderId newId() {
        return new OrderId(UUID.randomUUID());
    }

    public static OrderId from(UUID value) {
        return new OrderId(value);
    }
}
