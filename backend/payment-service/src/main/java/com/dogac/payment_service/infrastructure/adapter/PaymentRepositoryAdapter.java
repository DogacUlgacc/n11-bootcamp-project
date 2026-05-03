package com.dogac.payment_service.infrastructure.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.dogac.payment_service.domain.entities.Payment;
import com.dogac.payment_service.domain.repositories.PaymentRepository;
import com.dogac.payment_service.domain.valueobjects.OrderId;
import com.dogac.payment_service.domain.valueobjects.PaymentId;
import com.dogac.payment_service.infrastructure.mapper.PaymentEntityMapper;
import com.dogac.payment_service.infrastructure.repositories.SpringDataPaymentRepository;

@Component
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final SpringDataPaymentRepository springDataPaymentRepository;
    private final PaymentEntityMapper paymentEntityMapper;

    public PaymentRepositoryAdapter(
            SpringDataPaymentRepository springDataPaymentRepository,
            PaymentEntityMapper paymentEntityMapper) {
        this.springDataPaymentRepository = springDataPaymentRepository;
        this.paymentEntityMapper = paymentEntityMapper;
    }

    @Override
    public Payment save(Payment payment) {
        var entity = paymentEntityMapper.toEntity(payment);
        entity = springDataPaymentRepository.save(entity);
        return paymentEntityMapper.toDomain(entity);
    }

    @Override
    public Optional<Payment> findById(PaymentId id) {
        return springDataPaymentRepository.findById(id.value()).map(paymentEntityMapper::toDomain);
    }

    @Override
    public Optional<Payment> findByOrderId(OrderId orderId) {
        return springDataPaymentRepository.findByOrderId(orderId.value()).map(paymentEntityMapper::toDomain);
    }

    @Override
    public List<Payment> findAll() {
        return springDataPaymentRepository.findAll().stream()
                .map(paymentEntityMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByOrderId(OrderId orderId) {
        return springDataPaymentRepository.existsByOrderId(orderId.value());
    }
}
