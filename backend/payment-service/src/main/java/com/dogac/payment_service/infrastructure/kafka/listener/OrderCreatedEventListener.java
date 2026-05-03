package com.dogac.payment_service.infrastructure.kafka.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dogac.common_events.event.OrderCreatedEvent;
import com.dogac.payment_service.application.bus.CommandBus;
import com.dogac.payment_service.application.commands.CreatePendingPaymentCommand;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderCreatedEventListener {

    private final CommandBus commandBus;

    public OrderCreatedEventListener(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @KafkaListener(topics = "order-created", groupId = "payment-service")
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("OrderCreatedEvent received: {}", event);
        commandBus.send(new CreatePendingPaymentCommand(
                event.orderId(),
                event.userId(),
                event.totalAmount(),
                event.currency()));
        log.info("event işlendi");
    }
}
