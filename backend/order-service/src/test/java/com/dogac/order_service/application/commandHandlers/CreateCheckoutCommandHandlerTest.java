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

import com.dogac.common_events.event.OrderCreatedEvent;
import com.dogac.order_service.application.commands.CreateCheckoutCommand;
import com.dogac.order_service.application.dto.CreatedOrderResponse;
import com.dogac.order_service.application.feignDto.CartDto;
import com.dogac.order_service.application.feignDto.CartItemDto;
import com.dogac.order_service.application.feignDto.UserDto;
import com.dogac.order_service.application.mapper.CreateOrderMapper;
import com.dogac.order_service.application.port.CartPort;
import com.dogac.order_service.application.port.UserPort;
import com.dogac.order_service.domain.entities.Order;
import com.dogac.order_service.domain.enums.OrderStatus;
import com.dogac.order_service.domain.repositories.OrderRepository;
import com.dogac.order_service.domain.services.OrderDomainService;
import com.dogac.order_service.infrastructure.outbox.OutboxEventService;

@ExtendWith(MockitoExtension.class)
class CreateCheckoutCommandHandlerTest {

        @Mock
        private OrderRepository orderRepository;

        @Mock
        private UserPort userPort;

        @Mock
        private CartPort cartPort;

        @Mock
        private OutboxEventService outboxEventService;

        @Test
        void shouldCreateOrderOnCheckout() {
                CreateCheckoutCommandHandler handler = new CreateCheckoutCommandHandler(
                                outboxEventService,
                                new CreateOrderMapper(),
                                orderRepository,
                                new OrderDomainService(orderRepository),
                                userPort,
                                cartPort);
                UUID userId = UUID.randomUUID();
                UUID cartId = UUID.randomUUID();
                UUID productId = UUID.randomUUID();
                CreateCheckoutCommand command = new CreateCheckoutCommand(userId, cartId);
                when(userPort.getUserById(userId)).thenReturn(new UserDto(
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
                when(cartPort.getCartById(cartId)).thenReturn(new CartDto(
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

                ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
                verify(outboxEventService).saveOrderCreatedEvent(eventCaptor.capture());
                OrderCreatedEvent orderCreatedEvent = eventCaptor.getValue();
                assertEquals(savedOrder.getId().value(), orderCreatedEvent.orderId());
                assertEquals(userId, orderCreatedEvent.userId());
                assertEquals(cartId, orderCreatedEvent.cartId());
                assertEquals(BigDecimal.valueOf(200), orderCreatedEvent.totalAmount());
                assertEquals("TRY", orderCreatedEvent.currency());
        }
}
