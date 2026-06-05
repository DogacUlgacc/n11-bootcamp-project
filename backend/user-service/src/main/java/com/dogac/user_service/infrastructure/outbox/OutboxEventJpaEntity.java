package com.dogac.user_service.infrastructure.outbox;

import java.time.Instant;
import java.util.UUID;

import com.dogac.common_events.enums.OutboxStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "outbox_events")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEventJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false)
    private String aggregateType;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    @Column(nullable = false)
    private int retryCount;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant processedAt;

    private Instant nextRetryAt;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Version
    private Long version;

    public void markAsProcessed() {
        this.status = OutboxStatus.PROCESSED;
        this.processedAt = Instant.now();
        this.errorMessage = null;
    }

    public void markAsFailed(String errorMessage) {
        this.retryCount++;
        this.status = OutboxStatus.PENDING;
        this.errorMessage = errorMessage;
        this.nextRetryAt = Instant.now().plusSeconds(10);
    }
}