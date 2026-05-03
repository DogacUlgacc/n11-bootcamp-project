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

import com.dogac.common_events.event.PaymentSucceededEvent;
import com.dogac.order_service.domain.entities.Order;
import com.dogac.order_service.domain.enums.OrderStatus;
import com.dogac.order_service.domain.repositories.OrderRepository;
import com.dogac.order_service.domain.valueobjects.ExternalId;
import com.dogac.order_service.domain.valueobjects.OrderId;
import com.dogac.order_service.domain.valueobjects.OrderNumber;
import com.dogac.order_service.domain.valueobjects.UserId;

@ExtendWith(MockitoExtension.class)
class PaymentSucceededEventListenerTest {

    @Mock
    private OrderRepository orderRepository;

    @Test
    void shouldConfirmOrderWhenPaymentSucceededEventReceived() {
        PaymentSucceededEventListener listener = new PaymentSucceededEventListener(orderRepository);
        Order order = newOrder();
        PaymentSucceededEvent event = new PaymentSucceededEvent(
                UUID.randomUUID(),
                order.getId().value(),
                order.getUserId().value(),
                BigDecimal.valueOf(100),
                "TRY");
        when(orderRepository.findById(OrderId.from(event.orderId()))).thenReturn(Optional.of(order));

        listener.handlePaymentSucceeded(event);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(OrderStatus.CONFIRMED, orderCaptor.getValue().getStatus());
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
