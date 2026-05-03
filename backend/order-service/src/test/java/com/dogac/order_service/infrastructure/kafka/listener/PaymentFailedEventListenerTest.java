package com.dogac.order_service.infrastructure.kafka.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.dogac.common_events.event.PaymentFailedEvent;
import com.dogac.order_service.domain.entities.Order;
import com.dogac.order_service.domain.enums.OrderStatus;
import com.dogac.order_service.domain.repositories.OrderRepository;
import com.dogac.order_service.domain.valueobjects.ExternalId;
import com.dogac.order_service.domain.valueobjects.OrderId;
import com.dogac.order_service.domain.valueobjects.OrderNumber;
import com.dogac.order_service.domain.valueobjects.UserId;

@ExtendWith(MockitoExtension.class)
class PaymentFailedEventListenerTest {

    @Mock
    private OrderRepository orderRepository;

    @Test
    void shouldCancelOrderWhenPaymentFailedEventReceived() {
        PaymentFailedEventListener listener = new PaymentFailedEventListener(orderRepository);
        Order order = newOrder();
        PaymentFailedEvent event = new PaymentFailedEvent(UUID.randomUUID(), order.getId().value(), "declined");
        when(orderRepository.findById(OrderId.from(event.orderId()))).thenReturn(Optional.of(order));

        listener.handlePaymentSucceeded(event);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(OrderStatus.CANCELLED, orderCaptor.getValue().getStatus());
    }

    private Order newOrder() {
        return new Order(
                OrderId.newId(),
                new ExternalId("external-id"),
                OrderNumber.generate(),
                UserId.from(UUID.randomUUID()),
                List.of(),
                BigDecimal.ZERO,
                OrderStatus.CREATED,
                Instant.now(),
                Instant.now());
    }
}
