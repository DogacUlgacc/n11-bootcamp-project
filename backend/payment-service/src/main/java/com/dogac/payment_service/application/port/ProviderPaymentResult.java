package com.dogac.payment_service.application.port;

public record ProviderPaymentResult(
        boolean success,
        String providerPaymentId,
        String errorMessage) {
}