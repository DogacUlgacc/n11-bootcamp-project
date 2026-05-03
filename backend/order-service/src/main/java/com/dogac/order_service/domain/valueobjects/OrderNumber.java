package com.dogac.order_service.domain.valueobjects;

public record OrderNumber(String value) {

    public OrderNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Order number cannot be blank");
        }
    }

    public static OrderNumber generate() {
        return new OrderNumber("ORD-" + java.time.LocalDate.now() + "-" + java.util.UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase());
    }
}