package com.dogac.order_service.application.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dogac.order_service.application.dto.OrderItemResponse;
import com.dogac.order_service.application.dto.OrderResponse;
import com.dogac.order_service.domain.entities.Order;

@Component
public class OrderResponseMapper {
    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.productId().value(),
                        item.quantity(),
                        item.unitPrice(),
                        item.totalPrice()))
                .toList();
        return new OrderResponse(
                order.getId().value(),
                order.getOrderNumber().value(),
                order.getUserId().value(),
                items,
                order.getTotalAmount(),
                order.getStatus());
    }
}
