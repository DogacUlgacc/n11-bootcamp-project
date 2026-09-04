package com.dogac.order_service.infrastructure.kafka.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
import com.dogac.order_service.domain.valueobjects.OrderItem;
import com.dogac.order_service.domain.valueobjects.OrderNumber;
import com.dogac.order_service.domain.valueobjects.ProductId;
import com.dogac.order_service.domain.valueobjects.UserId;
import com.dogac.order_service.infrastructure.persistence.entity.ProcessedEventEntity;
import com.dogac.order_service.infrastructure.persistence.repository.ProcessedEventJpaRepository;

@ExtendWith(MockitoExtension.class)
class PaymentFailedEventListenerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProcessedEventJpaRepository processedEventJpaRepository;

    @Test
    void shouldCancelOrderWhenPaymentFailedEventReceived() {
        PaymentFailedEventListener listener = new PaymentFailedEventListener(
                orderRepository,
                processedEventJpaRepository);
        Order order = newOrder();
        PaymentFailedEvent event = new PaymentFailedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                order.getId().value(),
                "declined");
        when(orderRepository.findById(OrderId.from(event.orderId()))).thenReturn(Optional.of(order));

        listener.handlePaymentSucceeded(event);

        verify(processedEventJpaRepository).save(any(ProcessedEventEntity.class));
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(OrderStatus.CANCELLED, orderCaptor.getValue().getStatus());
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
