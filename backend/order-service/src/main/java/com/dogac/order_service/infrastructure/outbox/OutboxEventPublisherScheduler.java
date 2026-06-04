package com.dogac.order_service.infrastructure.outbox;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dogac.common_events.enums.OutboxStatus;
import com.dogac.common_events.event.OrderCreatedEvent;
import com.dogac.order_service.infrastructure.kafka.KafkaEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisherScheduler {
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaEventPublisher kafkaEventPublisher;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEventJpaEntity> events = outboxEventRepository.findPublishableEvents(OutboxStatus.PENDING,
                Instant.now(), PageRequest.of(0, 50));

        for (OutboxEventJpaEntity event : events) {
            publishEvent(event);
        }
    }

    private void publishEvent(OutboxEventJpaEntity outboxEvent) {
        try {
            if ("OrderCreatedEvent".equals(outboxEvent.getEventType())) {
                OrderCreatedEvent event = objectMapper.readValue(outboxEvent.getPayload(), OrderCreatedEvent.class);

                kafkaEventPublisher.publishOrderCreated(event);
            }

            outboxEvent.markAsProcessed();
            outboxEventRepository.save(outboxEvent);

            log.info("Outbox event Kafka'ya gönderildi. eventId={}", outboxEvent.getId());

        } catch (Exception e) {
            outboxEvent.markAsFailed(e.getMessage());
            outboxEventRepository.save(outboxEvent);

            log.error("Outbox event publish edilemedi. eventId={}", outboxEvent.getId(), e);
        }
    }
}
