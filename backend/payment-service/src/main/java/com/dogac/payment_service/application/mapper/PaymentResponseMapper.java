package com.dogac.payment_service.application.mapper;

import org.springframework.stereotype.Component;

import com.dogac.payment_service.application.dto.PaymentResponse;
import com.dogac.payment_service.domain.entities.Payment;

@Component
public class PaymentResponseMapper {

    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId().value(),
                payment.getOrderId().value(),
                payment.getPaymentProvider(),
                payment.getProviderPaymentId() != null ? payment.getProviderPaymentId().value() : null,
                payment.getMoney().amount(),
                payment.getMoney().currency().getCurrencyCode(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getUpdatedAt());
    }
}
