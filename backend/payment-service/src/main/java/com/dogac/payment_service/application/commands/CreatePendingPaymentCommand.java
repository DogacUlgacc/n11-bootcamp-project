package com.dogac.payment_service.application.commands;

import java.math.BigDecimal;
import java.util.UUID;

import com.dogac.payment_service.application.core.Command;
import com.dogac.payment_service.application.dto.CreatedPaymentResponse;

public record CreatePendingPaymentCommand(
        UUID orderId,
        UUID userId,
        BigDecimal amount,
        String currency) implements Command<CreatedPaymentResponse> {
}
