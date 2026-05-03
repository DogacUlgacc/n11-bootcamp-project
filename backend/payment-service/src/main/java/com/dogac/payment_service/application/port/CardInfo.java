package com.dogac.payment_service.application.port;

public record CardInfo(
        String cardHolderName,
        String cardNumber,
        String expireMonth,
        String expireYear,
        String cvc) {
}
