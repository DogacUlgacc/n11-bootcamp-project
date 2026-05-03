package com.dogac.payment_service.domain.valueobjects;

import java.util.Objects;

public record ProviderPaymentId(String value) {

    public ProviderPaymentId {
        Objects.requireNonNull(value, "Value for ProviderPaymentId cannot be null");

        if (value.isBlank()) {
            throw new IllegalArgumentException("ProviderPaymentId cannot be blank");
        }
    }

    public static ProviderPaymentId from(String value) {
        return new ProviderPaymentId(value);
    }
}
