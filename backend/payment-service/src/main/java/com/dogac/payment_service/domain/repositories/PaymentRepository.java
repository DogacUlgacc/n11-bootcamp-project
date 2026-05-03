package com.dogac.payment_service.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.dogac.payment_service.domain.entities.Payment;
import com.dogac.payment_service.domain.valueobjects.OrderId;
import com.dogac.payment_service.domain.valueobjects.PaymentId;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(PaymentId id);

    Optional<Payment> findByOrderId(OrderId orderId);

    List<Payment> findAll();

    boolean existsByOrderId(OrderId orderId);
}
