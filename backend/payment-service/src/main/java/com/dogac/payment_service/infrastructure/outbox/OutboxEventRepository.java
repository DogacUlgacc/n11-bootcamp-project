package com.dogac.payment_service.infrastructure.outbox;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dogac.common_events.enums.OutboxStatus;

public interface OutboxEventRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {
  @Query("""
      select e from OutboxEventJpaEntity e
      where e.status = :status
        and (e.nextRetryAt is null or e.nextRetryAt <= :now)
      order by e.createdAt asc
      """)
  List<OutboxEventJpaEntity> findPublishableEvents(
      @Param("status") OutboxStatus status,
      @Param("now") Instant now,
      Pageable pageable);

}
