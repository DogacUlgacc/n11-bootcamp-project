package com.dogac.order_service.infrastructure.kafka.listener;

import java.time.Instant;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dogac.common_events.event.PaymentFailedEvent;
import com.dogac.order_service.domain.entities.Order;
import com.dogac.order_service.domain.exceptions.OrderNotFoundException;
import com.dogac.order_service.domain.repositories.OrderRepository;
import com.dogac.order_service.domain.valueobjects.OrderId;
import com.dogac.order_service.infrastructure.persistence.entity.ProcessedEventEntity;
import com.dogac.order_service.infrastructure.persistence.repository.ProcessedEventJpaRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaymentFailedEventListener {

    private final OrderRepository orderRepository;
    private final ProcessedEventJpaRepository processedEventJpaRepository;

    public PaymentFailedEventListener(OrderRepository orderRepository,
            ProcessedEventJpaRepository processedEventJpaRepository) {
        this.orderRepository = orderRepository;
        this.processedEventJpaRepository = processedEventJpaRepository;
    }

    @KafkaListener(topics = "payment-failed", groupId = "order-service")
    @Transactional
    public void handlePaymentSucceeded(PaymentFailedEvent event) {
        log.info("payment-failed event: {} " + event);
        processedEventJpaRepository.save(
                new ProcessedEventEntity(event.eventId(), Instant.now()));
        Order order = orderRepository.findById(OrderId.from(event.orderId()))
                .orElseThrow(() -> new OrderNotFoundException("OrderNotFound!"));
        order.cancel();

        orderRepository.save(order);
    }

}
