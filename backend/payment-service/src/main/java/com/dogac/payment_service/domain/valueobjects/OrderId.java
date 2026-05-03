package com.dogac.payment_service.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record OrderId(UUID value) {

    public OrderId {
        Objects.requireNonNull(value, "Value for OrderId cannot be null");
    }

    public static OrderId from(UUID value) {
        return new OrderId(value);
    }
}
