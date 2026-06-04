package com.dogac.payment_service.infrastructure.outbox;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dogac.common_events.enums.OutboxStatus;
import com.dogac.common_events.event.PaymentFailedEvent;
import com.dogac.common_events.event.PaymentSucceededEvent;
import com.dogac.common_events.exceptions.EventSerializationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public void savePaymentSucceededEvent(PaymentSucceededEvent event) {
        try {
            OutboxEventJpaEntity outboxEvent = OutboxEventJpaEntity.builder()
                    .id(UUID.randomUUID())
                    .aggregateId(event.orderId())
                    .aggregateType("PAYMENT")
                    .eventType("PaymentSucceededEvent")
                    .payload(objectMapper.writeValueAsString(event))
                    .status(OutboxStatus.PENDING)
                    .retryCount(0)
                    .createdAt(Instant.now())
                    .build();

            outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new EventSerializationException(PaymentSucceededEvent.class, e);
        }
    }

    public void savePaymentFailedEvent(PaymentFailedEvent event) {
        try {
            OutboxEventJpaEntity outboxEvent = OutboxEventJpaEntity.builder()
                    .id(UUID.randomUUID())
                    .aggregateId(event.orderId())
                    .aggregateType("PAYMENT")
                    .eventType("PaymentFailedEvent")
                    .payload(objectMapper.writeValueAsString(event))
                    .status(OutboxStatus.PENDING)
                    .retryCount(0)
                    .createdAt(Instant.now())
                    .build();

            outboxEventRepository.save(outboxEvent);

        } catch (JsonProcessingException e) {
            throw new EventSerializationException(PaymentFailedEvent.class, e);
        }
    }
}
