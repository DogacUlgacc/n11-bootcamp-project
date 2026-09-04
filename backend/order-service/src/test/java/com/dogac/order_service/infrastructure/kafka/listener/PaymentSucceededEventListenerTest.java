package com.dogac.order_service.infrastructure.kafka.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dogac.common_events.event.PaymentSucceededEvent;
import com.dogac.order_service.domain.entities.Order;
import com.dogac.order_service.domain.enums.OrderStatus;
import com.dogac.order_service.domain.repositories.OrderRepository;
import com.dogac.order_service.domain.valueobjects.ExternalId;
import com.dogac.order_service.domain.valueobjects.OrderId;
import com.dogac.order_service.domain.valueobjects.OrderItem;
import com.dogac.order_service.domain.valueobjects.OrderNumber;
import com.dogac.order_service.domain.valueobjects.ProductId;
import com.dogac.order_service.domain.valueobjects.UserId;
import com.dogac.order_service.infrastructure.persistence.entity.ProcessedEventEntity;
import com.dogac.order_service.infrastructure.persistence.repository.ProcessedEventJpaRepository;

@ExtendWith(MockitoExtension.class)
class PaymentSucceededEventListenerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProcessedEventJpaRepository processedEventJpaRepository;

    @Test
    void shouldConfirmOrderWhenPaymentSucceededEventReceived() {
        PaymentSucceededEventListener listener = new PaymentSucceededEventListener(
                orderRepository,
                processedEventJpaRepository);
        Order order = newOrder();
        UUID eventId = UUID.randomUUID();
        PaymentSucceededEvent event = new PaymentSucceededEvent(
                eventId,
                UUID.randomUUID(),
                order.getId().value(),
                order.getUserId().value(),
                BigDecimal.valueOf(100),
                "TRY");
        when(processedEventJpaRepository.existsByEventId(eventId)).thenReturn(false);
        when(orderRepository.findById(OrderId.from(event.orderId()))).thenReturn(Optional.of(order));

        listener.handlePaymentSucceeded(event);

        verify(processedEventJpaRepository).save(any(ProcessedEventEntity.class));
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(OrderStatus.CONFIRMED, orderCaptor.getValue().getStatus());
    }

    @Test
    void shouldIgnoreDuplicatePaymentSucceededEvent() {
        PaymentSucceededEventListener listener = new PaymentSucceededEventListener(
                orderRepository,
                processedEventJpaRepository);
        UUID eventId = UUID.randomUUID();
        PaymentSucceededEvent event = new PaymentSucceededEvent(
                eventId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(100),
                "TRY");
        when(processedEventJpaRepository.existsByEventId(eventId)).thenReturn(true);

        listener.handlePaymentSucceeded(event);

        verify(processedEventJpaRepository, never()).save(any(ProcessedEventEntity.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    private Order newOrder() {
        UUID productId = UUID.randomUUID();
        List<OrderItem> items = List.of(
                new OrderItem(ProductId.from(productId), 1, BigDecimal.valueOf(100), null));
        return new Order(
                OrderId.newId(),
                new ExternalId("external-id"),
                OrderNumber.generate(),
                UserId.from(UUID.randomUUID()),
                items,
                null,
                OrderStatus.CREATED,
                Instant.now(),
                Instant.now());
    }
}
