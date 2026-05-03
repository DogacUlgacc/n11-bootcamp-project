package com.dogac.payment_service.domain.entities;

import java.time.Instant;
import java.util.Objects;

import com.dogac.payment_service.domain.core.AggregateRoot;
import com.dogac.payment_service.domain.enums.PaymentProvider;
import com.dogac.payment_service.domain.enums.PaymentStatus;
import com.dogac.payment_service.domain.valueobjects.Money;
import com.dogac.payment_service.domain.valueobjects.OrderId;
import com.dogac.payment_service.domain.valueobjects.PaymentId;
import com.dogac.payment_service.domain.valueobjects.ProviderPaymentId;

public class Payment implements AggregateRoot<PaymentId> {

    private final PaymentId id;
    private final OrderId orderId;
    private final PaymentProvider paymentProvider;
    private Money money;
    private ProviderPaymentId providerPaymentId;
    private PaymentStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    private Payment(
            PaymentId id,
            OrderId orderId,
            PaymentProvider paymentProvider,
            Money money,
            ProviderPaymentId providerPaymentId,
            PaymentStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.orderId = Objects.requireNonNull(orderId);
        this.paymentProvider = Objects.requireNonNull(paymentProvider);
        this.money = Objects.requireNonNull(money);
        this.providerPaymentId = providerPaymentId;
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static Payment create(OrderId orderId, Money money) {
        Instant now = Instant.now();

        return new Payment(
                PaymentId.generate(),
                Objects.requireNonNull(orderId),
                PaymentProvider.IYZICO,
                Objects.requireNonNull(money),
                null,
                PaymentStatus.PENDING,
                now,
                now);
    }

    public static Payment rehydrate(
            PaymentId id,
            OrderId orderId,
            PaymentProvider paymentProvider,
            Money money,
            ProviderPaymentId providerPaymentId,
            PaymentStatus status,
            Instant createdAt,
            Instant updatedAt) {
        return new Payment(id, orderId, paymentProvider, money, providerPaymentId, status, createdAt, updatedAt);
    }

    public void attachProviderPaymentId(ProviderPaymentId providerPaymentId) {
        this.providerPaymentId = Objects.requireNonNull(providerPaymentId);
        touch();
    }

    public void complete() {
        if (status == PaymentStatus.COMPLETED) {
            return;
        }

        status = PaymentStatus.COMPLETED;
        touch();
    }

    public void complete(String providerPaymentId) {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Only pending payment can be completed");
        }

        this.status = PaymentStatus.COMPLETED;
        this.providerPaymentId = ProviderPaymentId.from(providerPaymentId);
        this.updatedAt = Instant.now();
    }

    public void fail() {
        if (status == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Completed payment cannot be failed");
        }

        status = PaymentStatus.FAILED;
        touch();
    }

    public void fail(String reason) {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Only pending payment can be failed");
        }
        this.status = PaymentStatus.FAILED;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        if (status == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Completed payment cannot be cancelled");
        }

        status = PaymentStatus.CANCELLED;
        touch();
    }

    @Override
    public PaymentId getId() {
        return id;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public PaymentProvider getPaymentProvider() {
        return paymentProvider;
    }

    public Money getMoney() {
        return money;
    }

    public ProviderPaymentId getProviderPaymentId() {
        return providerPaymentId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private void touch() {
        updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment other)) {
            return false;
        }
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
