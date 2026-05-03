package com.dogac.payment_service.infrastructure.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dogac.payment_service.infrastructure.entities.JpaPaymentEntity;

public interface SpringDataPaymentRepository extends JpaRepository<JpaPaymentEntity, UUID> {

    Optional<JpaPaymentEntity> findByOrderId(UUID orderId);

    boolean existsByOrderId(UUID orderId);
}
