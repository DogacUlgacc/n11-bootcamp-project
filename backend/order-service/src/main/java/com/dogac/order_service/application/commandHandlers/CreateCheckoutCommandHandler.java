package com.dogac.order_service.application.commandHandlers;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import com.dogac.common_events.event.OrderCreatedEvent;
import com.dogac.order_service.application.commands.CreateCheckoutCommand;
import com.dogac.order_service.application.core.CommandHandler;
import com.dogac.order_service.application.dto.CreatedOrderResponse;
import com.dogac.order_service.application.feignDto.CartDto;
import com.dogac.order_service.application.feignDto.UserDto;
import com.dogac.order_service.application.mapper.CreateOrderMapper;
import com.dogac.order_service.domain.entities.Order;
import com.dogac.order_service.domain.enums.OrderStatus;
import com.dogac.order_service.domain.repositories.OrderRepository;
import com.dogac.order_service.domain.services.OrderDomainService;
import com.dogac.order_service.domain.valueobjects.ExternalId;
import com.dogac.order_service.domain.valueobjects.OrderId;
import com.dogac.order_service.domain.valueobjects.OrderItem;
import com.dogac.order_service.domain.valueobjects.OrderNumber;
import com.dogac.order_service.domain.valueobjects.UserId;
import com.dogac.order_service.infrastructure.feignclients.CartClient;
import com.dogac.order_service.infrastructure.feignclients.UserClient;
import com.dogac.order_service.infrastructure.kafka.KafkaEventPublisher;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CreateCheckoutCommandHandler implements CommandHandler<CreateCheckoutCommand, CreatedOrderResponse> {

    private final CreateOrderMapper createOrderMapper;
    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
    private final UserClient userClient;
    private final CartClient cartClients;
    private final KafkaEventPublisher kafkaEventPublisher;

    public CreateCheckoutCommandHandler(CreateOrderMapper createOrderMapper, OrderRepository orderRepository,
            OrderDomainService orderDomainService, UserClient userClient, CartClient cartClients,
            KafkaEventPublisher kafkaEventPublisher) {
        this.createOrderMapper = createOrderMapper;
        this.orderRepository = orderRepository;
        this.orderDomainService = orderDomainService;
        this.userClient = userClient;
        this.cartClients = cartClients;
        this.kafkaEventPublisher = kafkaEventPublisher;
    }

    public CreatedOrderResponse handle(CreateCheckoutCommand command) {
        UserDto userDto = userClient.getUserById(command.userId());
        if (userDto == null) {
            throw new RuntimeException("usernotfound");
        }
        log.info("userDto: " + userDto);
        CartDto cartDto = cartClients.getCartById(command.cartId());

        log.info("cartdto: " + cartDto);
        List<OrderItem> orderItems = cartDto.items()
                .stream()
                .map(OrderItem::fromCartItem)
                .toList();

        OrderNumber orderNumber = OrderNumber.generate();
        orderDomainService.ensureOrderNumberIsUnique(orderNumber);

        Order order = new Order(
                OrderId.newId(),
                new ExternalId("placeholder-external-id"),
                orderNumber,
                UserId.from(command.userId()),
                orderItems,
                null,
                OrderStatus.CREATED,
                Instant.now(),
                Instant.now());

        Order saved = orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId().value(),
                order.getUserId().value(),
                cartDto.cartId(),
                saved.getTotalAmount(), cartDto.currency());
        log.info("orderCreatedEvent:" + event);
        kafkaEventPublisher.publisOrderCreated(event);
        log.info("Event gönderildi");
        return createOrderMapper.toResponse(saved);
    }
}
