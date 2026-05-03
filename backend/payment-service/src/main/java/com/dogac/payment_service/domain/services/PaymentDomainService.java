package com.dogac.payment_service.domain.services;

import com.dogac.payment_service.domain.exceptions.DuplicatePaymentForOrderException;
import com.dogac.payment_service.domain.repositories.PaymentRepository;
import com.dogac.payment_service.domain.valueobjects.OrderId;

public class PaymentDomainService {

    private final PaymentRepository paymentRepository;

    public PaymentDomainService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public void ensureOrderHasNoPayment(OrderId orderId) {
        if (paymentRepository.existsByOrderId(orderId)) {
            throw new DuplicatePaymentForOrderException("Payment already exists for order");
        }
    }
}
