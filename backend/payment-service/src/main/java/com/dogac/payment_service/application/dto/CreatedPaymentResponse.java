package com.dogac.payment_service.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.dogac.payment_service.domain.enums.PaymentProvider;
import com.dogac.payment_service.domain.enums.PaymentStatus;

public record CreatedPaymentResponse(
        UUID id,
        UUID orderId,
        PaymentProvider paymentProvider,
        String providerPaymentId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        Instant createdAt) {
}
