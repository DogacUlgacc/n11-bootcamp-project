package com.dogac.order_service.infrastructure.kafka.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dogac.common_events.event.PaymentFailedEvent;
import com.dogac.order_service.domain.entities.Order;
import com.dogac.order_service.domain.exceptions.OrderNotFoundException;
import com.dogac.order_service.domain.repositories.OrderRepository;
import com.dogac.order_service.domain.valueobjects.OrderId;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaymentFailedEventListener {

    private final OrderRepository orderRepository;

    public PaymentFailedEventListener(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "payment-failed", groupId = "order-service")
    @Transactional
    public void handlePaymentSucceeded(PaymentFailedEvent event) {
        log.info("payment-failed event: {} " + event);
        Order order = orderRepository.findById(OrderId.from(event.orderId()))
                .orElseThrow(() -> new OrderNotFoundException("OrderNotFound!"));
        order.cancel();
        orderRepository.save(order);
    }

}
