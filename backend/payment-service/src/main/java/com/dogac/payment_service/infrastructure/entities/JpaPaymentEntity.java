package com.dogac.payment_service.infrastructure.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.dogac.payment_service.domain.enums.PaymentProvider;
import com.dogac.payment_service.domain.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments")
public class JpaPaymentEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentProvider paymentProvider;

    @Column
    private String providerPaymentId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected JpaPaymentEntity() {
    }

    public JpaPaymentEntity(
            UUID id,
            UUID orderId,
            PaymentProvider paymentProvider,
            String providerPaymentId,
            BigDecimal amount,
            String currency,
            PaymentStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.orderId = orderId;
        this.paymentProvider = paymentProvider;
        this.providerPaymentId = providerPaymentId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public PaymentProvider getPaymentProvider() {
        return paymentProvider;
    }

    public String getProviderPaymentId() {
        return providerPaymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
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
}
