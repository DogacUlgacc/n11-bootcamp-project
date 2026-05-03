package com.dogac.order_service.infrastructure.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.dogac.common_events.event.OrderCreatedEvent;

@Component
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publisOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send("order-created", event);
    }
}
