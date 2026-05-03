package com.dogac.payment_service.application.dto;

public record PayWithIyzicoRequest(
        String cardHolderName,
        String cardNumber,
        String expireMonth,
        String expireYear,
        String cvc) {
}
