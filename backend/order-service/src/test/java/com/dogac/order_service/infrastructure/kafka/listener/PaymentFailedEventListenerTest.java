package com.dogac.order_service.infrastructure.kafka.listener;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dogac.order_service.domain.repositories.OrderRepository;
import com.dogac.order_service.infrastructure.persistence.repository.ProcessedEventJpaRepository;

@ExtendWith(MockitoExtension.class)
class PaymentFailedEventListenerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProcessedEventJpaRepository processedEventJpaRepository;

    // @Test
    // void shouldCancelOrderWhenPaymentFailedEventReceived() {
    // PaymentFailedEventListener listener = new
    // PaymentFailedEventListener(orderRepository, ProcessedEventJpaRepository
    // processedEventJpaRepository;);
    // Order order = newOrder();
    // PaymentFailedEvent event = new
    // PaymentFailedEvent(UUID.randomUUID(),UUID.randomUUID(),
    // order.getId().value(), "declined");
    // when(orderRepository.findById(OrderId.from(event.orderId()))).thenReturn(Optional.of(order));

    // listener.handlePaymentSucceeded(event);

    // ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
    // verify(orderRepository).save(orderCaptor.capture());
    // assertEquals(OrderStatus.CANCELLED, orderCaptor.getValue().getStatus());
    // }

    // private Order newOrder() {
    // return new Order(
    // OrderId.newId(),
    // new ExternalId("external-id"),
    // OrderNumber.generate(),
    // UserId.from(UUID.randomUUID()),
    // List.of(),
    // BigDecimal.ZERO,
    // OrderStatus.CREATED,
    // Instant.now(),
    // Instant.now());
    // }
}
