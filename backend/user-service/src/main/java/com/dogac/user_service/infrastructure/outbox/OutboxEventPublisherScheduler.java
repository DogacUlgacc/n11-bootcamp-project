package com.dogac.user_service.infrastructure.outbox;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dogac.common_events.enums.OutboxStatus;
import com.dogac.common_events.event.UserRegisteredEvent;
import com.dogac.user_service.infrastructure.kafka.KafkaEventPublisher;
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
        List<OutboxEventJpaEntity> events = outboxEventRepository.findPublishableEvents(
                OutboxStatus.PENDING, Instant.now(), PageRequest.of(0, 50));
        for (OutboxEventJpaEntity event : events) {
            publishEvent(event);
        }
    }

    private void publishEvent(OutboxEventJpaEntity outboxEvent) {
        try {

            // 1. Deserialize + route
            if ("UserRegisteredEvent".equals(outboxEvent.getEventType())) {

                UserRegisteredEvent event = objectMapper.readValue(outboxEvent.getPayload(), UserRegisteredEvent.class);

                // 2. Publish to Kafka (cart-service consumer will handle it)
                kafkaEventPublisher.publishUserRegistered(event);
            } else {
                log.warn("Unknown event type: {}", outboxEvent.getEventType());
                return;
            }

            // 3. SUCCESS → mark processed
            outboxEvent.markAsProcessed();
            outboxEventRepository.save(outboxEvent);

            log.info("Outbox event processed successfully. id={}", outboxEvent.getId());

        } catch (Exception e) {

            // 4. FAIL → retry logic
            outboxEvent.markAsFailed(e.getMessage());
            outboxEventRepository.save(outboxEvent);

            log.error("Outbox event publish failed. id={}", outboxEvent.getId(), e);
        }
    }
}