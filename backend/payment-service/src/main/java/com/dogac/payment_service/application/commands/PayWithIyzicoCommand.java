package com.dogac.payment_service.application.commands;

import java.util.UUID;

import com.dogac.payment_service.application.core.Command;
import com.dogac.payment_service.application.dto.PaymentResponse;

public record PayWithIyzicoCommand(
        UUID paymentId,
        String cardHolderName,
        String cardNumber,
        String expireMonth,
        String expireYear,
        String cvc) implements Command<PaymentResponse> {
}