package com.dogac.order_service.domain.valueobjects;

import java.util.UUID;

public record UserId(UUID value) {
    public static UserId from(UUID value) {
        return new UserId(value);
    }
}
