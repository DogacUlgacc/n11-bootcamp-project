package com.dogac.order_service.domain.valueobjects;

import java.util.UUID;

public record ProductId(UUID value) {
    public static ProductId from(UUID value) {
        return new ProductId(value);
    }
}
