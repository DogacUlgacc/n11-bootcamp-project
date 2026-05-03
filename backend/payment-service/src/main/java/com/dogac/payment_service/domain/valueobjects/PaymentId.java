package com.dogac.payment_service.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record PaymentId(UUID value) {

    public PaymentId {
        Objects.requireNonNull(value, "Value for PaymentId cannot be null");
    }

    public static PaymentId generate() {
        return new PaymentId(UUID.randomUUID());
    }

    public static PaymentId from(UUID value) {
        return new PaymentId(value);
    }
}
