package com.dogac.order_service.infrastructure.outbox;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dogac.common_events.enums.OutboxStatus;
import com.dogac.common_events.event.OrderCreatedEvent;
import com.dogac.common_events.exceptions.EventSerializationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public void saveOrderCreatedEvent(OrderCreatedEvent event) {
        try {
            OutboxEventJpaEntity outboxEvent = OutboxEventJpaEntity.builder()
                    .id(UUID.randomUUID())
                    .aggregateId(event.orderId())
                    .aggregateType("ORDER")
                    .eventType("OrderCreatedEvent")
                    .payload(objectMapper.writeValueAsString(event))
                    .status(OutboxStatus.PENDING)
                    .retryCount(0)
                    .createdAt(Instant.now())
                    .build();

            outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new EventSerializationException(OrderCreatedEvent.class, e);
        }
    }
}
