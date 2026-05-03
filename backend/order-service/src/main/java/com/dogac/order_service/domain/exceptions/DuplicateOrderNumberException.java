package com.dogac.order_service.domain.exceptions;

public class DuplicateOrderNumberException extends RuntimeException {
    public DuplicateOrderNumberException(String message) {
        super(message);
    }
}
