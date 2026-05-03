package com.dogac.payment_service.infrastructure.kafka.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.dogac.common_events.event.PaymentFailedEvent;
import com.dogac.common_events.event.PaymentSucceededEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPaymentSucceeded(PaymentSucceededEvent event) {
        log.info("Publishing PaymentSucceededEvent: {}", event);
        kafkaTemplate.send(
                "payment-succeeded",
                event.orderId().toString(),
                event);
    }

    public void publishPaymentFailed(PaymentFailedEvent event) {
        log.info("Publishing PaymentFailedEvent: {}", event);
        kafkaTemplate.send("payment-failed",
                event.orderId().toString(),
                event);
    }
}
