package com.dogac.payment_service.application.commands;

import java.util.UUID;

import com.dogac.payment_service.application.core.Command;
import com.dogac.payment_service.application.dto.PaymentResponse;

public record CompletePaymentCommand(
        UUID paymentId,
        String providerPaymentId) implements Command<PaymentResponse> {

}
