package com.dogac.payment_service.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dogac.payment_service.infrastructure.persistence.entity.ProcessedEventEntity;

public interface ProcessedEventJpaRepository
        extends JpaRepository<ProcessedEventEntity, UUID> {

    boolean existsByEventId(UUID eventId);
}