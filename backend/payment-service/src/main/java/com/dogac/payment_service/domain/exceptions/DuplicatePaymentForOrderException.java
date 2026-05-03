package com.dogac.payment_service.domain.exceptions;

public class DuplicatePaymentForOrderException extends RuntimeException {

    public DuplicatePaymentForOrderException(String message) {
        super(message);
    }
}
