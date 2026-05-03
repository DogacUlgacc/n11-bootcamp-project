package com.dogac.order_service.infrastructure.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dogac.order_service.infrastructure.entities.JpaOrderEntity;

@Repository
public interface SpringDataOrderRepository extends JpaRepository<JpaOrderEntity, UUID> {
    boolean existsByOrderNumber(String orderNumber);
}
