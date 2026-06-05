package com.dogac.user_service.infrastructure.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.dogac.common_events.event.UserRegisteredEvent;

@Component
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishUserRegistered(UserRegisteredEvent event) {
        try {
            kafkaTemplate.send("user-registered", event).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish UserRegisteredEvent", e);
        }
    }
}
