package com.dogac.payment_service.application.mapper;

import org.springframework.stereotype.Component;

import com.dogac.payment_service.application.commands.CreatePendingPaymentCommand;
import com.dogac.payment_service.application.dto.CreatedPaymentResponse;
import com.dogac.payment_service.domain.entities.Payment;
import com.dogac.payment_service.domain.valueobjects.Money;
import com.dogac.payment_service.domain.valueobjects.OrderId;

@Component
public class CreatePaymentMapper {

    public Payment toEntity(CreatePendingPaymentCommand command) {
        return Payment.create(
                OrderId.from(command.orderId()),
                Money.of(command.amount(), command.currency()));
    }

    public CreatedPaymentResponse toResponse(Payment payment) {
        return new CreatedPaymentResponse(
                payment.getId().value(),
                payment.getOrderId().value(),
                payment.getPaymentProvider(),
                payment.getProviderPaymentId() != null ? payment.getProviderPaymentId().value() : null,
                payment.getMoney().amount(),
                payment.getMoney().currency().getCurrencyCode(),
                payment.getStatus(),
                payment.getCreatedAt());
    }
}
