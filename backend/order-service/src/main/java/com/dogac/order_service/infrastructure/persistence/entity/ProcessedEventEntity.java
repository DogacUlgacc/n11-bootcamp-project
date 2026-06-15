package com.dogac.order_service.infrastructure.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "processed_events")

public class ProcessedEventEntity {

    protected ProcessedEventEntity() {
    }

    public ProcessedEventEntity(UUID eventId, Instant processedAt) {
        this.id = UUID.randomUUID();
        this.eventId = eventId;
        this.processedAt = processedAt;
    }

    @Id
    private UUID id;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    private Instant processedAt;
}
