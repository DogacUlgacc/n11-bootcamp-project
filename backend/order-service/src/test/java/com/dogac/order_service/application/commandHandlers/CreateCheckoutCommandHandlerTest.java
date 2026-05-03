package com.dogac.order_service.application.commandHandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.dogac.common_events.event.OrderCreatedEvent;
import com.dogac.order_service.application.commands.CreateCheckoutCommand;
import com.dogac.order_service.application.dto.CreatedOrderResponse;
import com.dogac.order_service.application.feignDto.CartDto;
import com.dogac.order_service.application.feignDto.CartItemDto;
import com.dogac.order_service.application.feignDto.UserDto;
import com.dogac.order_service.application.mapper.CreateOrderMapper;
import com.dogac.order_service.domain.entities.Order;
import com.dogac.order_service.domain.enums.OrderStatus;
import com.dogac.order_service.domain.repositories.OrderRepository;
import com.dogac.order_service.domain.services.OrderDomainService;
import com.dogac.order_service.infrastructure.feignclients.CartClient;
import com.dogac.order_service.infrastructure.feignclients.UserClient;
import com.dogac.order_service.infrastructure.kafka.KafkaEventPublisher;

@ExtendWith(MockitoExtension.class)
class CreateCheckoutCommandHandlerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private CartClient cartClient;

    @Test
    void shouldCreateOrderOnCheckout() {
        TestKafkaEventPublisher kafkaEventPublisher = new TestKafkaEventPublisher();
        CreateCheckoutCommandHandler handler = new CreateCheckoutCommandHandler(
                new CreateOrderMapper(),
                orderRepository,
                new OrderDomainService(orderRepository),
                userClient,
                cartClient,
                kafkaEventPublisher);
        UUID userId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        CreateCheckoutCommand command = new CreateCheckoutCommand(userId, cartId);
        when(userClient.getUserById(userId)).thenReturn(new UserDto(
                userId,
                UUID.randomUUID(),
                "Dogac",
                "Test",
                "dogac@example.com",
                "+905551234567",
                "CUSTOMER",
                "ACTIVE",
                List.of(),
                Instant.now(),
                Instant.now()));
        when(cartClient.getCartById(cartId)).thenReturn(new CartDto(
                cartId,
                userId,
                "TRY",
                List.of(new CartItemDto(
                        UUID.randomUUID(),
                        productId,
                        2,
                        BigDecimal.valueOf(100),
                        "TRY"))));
        when(orderRepository.existsByOrderNumber(any())).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreatedOrderResponse response = handler.handle(command);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        assertNotNull(savedOrder.getId());
        assertEquals(userId, savedOrder.getUserId().value());
        assertEquals(OrderStatus.CREATED, savedOrder.getStatus());
        assertEquals(1, savedOrder.getItems().size());
        assertEquals(productId, savedOrder.getItems().get(0).productId().value());
        assertEquals(BigDecimal.valueOf(200), savedOrder.getTotalAmount());
        assertEquals(savedOrder.getId().value(), response.id());

        assertEquals(savedOrder.getId().value(), kafkaEventPublisher.orderCreatedEvent.orderId());
        assertEquals(userId, kafkaEventPublisher.orderCreatedEvent.userId());
        assertEquals(cartId, kafkaEventPublisher.orderCreatedEvent.cartId());
        assertEquals(BigDecimal.valueOf(200), kafkaEventPublisher.orderCreatedEvent.totalAmount());
        assertEquals("TRY", kafkaEventPublisher.orderCreatedEvent.currency());
    }

    private static class TestKafkaEventPublisher extends KafkaEventPublisher {
        private OrderCreatedEvent orderCreatedEvent;

        TestKafkaEventPublisher() {
            super((KafkaTemplate<String, Object>) null);
        }

        @Override
        public void publisOrderCreated(OrderCreatedEvent event) {
            this.orderCreatedEvent = event;
        }
    }
}
