package com.dogac.payment_service.infrastructure.mapper;

import org.springframework.stereotype.Component;

import com.dogac.payment_service.domain.entities.Payment;
import com.dogac.payment_service.domain.valueobjects.Money;
import com.dogac.payment_service.domain.valueobjects.OrderId;
import com.dogac.payment_service.domain.valueobjects.PaymentId;
import com.dogac.payment_service.domain.valueobjects.ProviderPaymentId;
import com.dogac.payment_service.infrastructure.entities.JpaPaymentEntity;

@Component
public class PaymentEntityMapper {

    public JpaPaymentEntity toEntity(Payment payment) {
        return new JpaPaymentEntity(
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

    public Payment toDomain(JpaPaymentEntity entity) {
        return Payment.rehydrate(
                PaymentId.from(entity.getId()),
                OrderId.from(entity.getOrderId()),
                entity.getPaymentProvider(),
                Money.of(entity.getAmount(), entity.getCurrency()),
                entity.getProviderPaymentId() != null ? ProviderPaymentId.from(entity.getProviderPaymentId()) : null,
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
