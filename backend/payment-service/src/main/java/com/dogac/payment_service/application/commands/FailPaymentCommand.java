package com.dogac.payment_service.application.commands;

import java.util.UUID;

import com.dogac.payment_service.application.core.Command;
import com.dogac.payment_service.application.dto.PaymentResponse;

public record FailPaymentCommand(
        UUID paymentId,
        String reason) implements Command<PaymentResponse> {

}
