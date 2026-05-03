package com.dogac.order_service.application.mapper;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import com.dogac.order_service.application.commands.CreateOrderCommand;
import com.dogac.order_service.application.dto.CreatedOrderResponse;
import com.dogac.order_service.domain.entities.Order;
import com.dogac.order_service.domain.enums.OrderStatus;
import com.dogac.order_service.domain.valueobjects.ExternalId;
import com.dogac.order_service.domain.valueobjects.OrderItem;
import com.dogac.order_service.domain.valueobjects.OrderId;
import com.dogac.order_service.domain.valueobjects.OrderNumber;
import com.dogac.order_service.domain.valueobjects.ProductId;
import com.dogac.order_service.domain.valueobjects.UserId;

@Component
public class CreateOrderMapper {
    public Order toEntity(CreateOrderCommand command, OrderNumber orderNumber) {
        return new Order(
                OrderId.newId(),
                new ExternalId("placeholder-external-id"),
                orderNumber,
                UserId.from(command.userId()),
                mapItems(command),
                null,
                OrderStatus.CREATED,
                Instant.now(),
                Instant.now());
    }

    public CreatedOrderResponse toResponse(Order order) {
        return new CreatedOrderResponse(order.getId().value());
    }

    private List<OrderItem> mapItems(CreateOrderCommand command) {
        return command.items().stream()
                .map(item -> new OrderItem(ProductId.from(item.productId()), item.quantity(), item.unitPrice(), null))
                .toList();
    }
}
