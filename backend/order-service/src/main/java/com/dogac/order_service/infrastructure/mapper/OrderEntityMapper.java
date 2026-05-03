package com.dogac.order_service.infrastructure.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dogac.order_service.domain.entities.Order;
import com.dogac.order_service.domain.valueobjects.ExternalId;
import com.dogac.order_service.domain.valueobjects.OrderItem;
import com.dogac.order_service.domain.valueobjects.OrderId;
import com.dogac.order_service.domain.valueobjects.OrderNumber;
import com.dogac.order_service.domain.valueobjects.ProductId;
import com.dogac.order_service.domain.valueobjects.UserId;
import com.dogac.order_service.infrastructure.entities.JpaOrderEntity;
import com.dogac.order_service.infrastructure.entities.OrderItemEmbeddable;

@Component
public class OrderEntityMapper {
    public JpaOrderEntity toEntity(Order domain) {
        JpaOrderEntity entity = new JpaOrderEntity();
        entity.setId(domain.getId().value());
        entity.setExternalId(domain.getExternalId() != null ? domain.getExternalId().value() : null);
        entity.setOrderNumber(domain.getOrderNumber().value());
        entity.setUserId(domain.getUserId().value());
        entity.setItems(domain.getItems().stream()
                .map(item -> new OrderItemEmbeddable(
                        item.productId().value(),
                        item.quantity(),
                        item.unitPrice(),
                        item.totalPrice()))
                .toList());
        entity.setTotalAmount(domain.getTotalAmount());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public Order toDomain(JpaOrderEntity entity) {
        List<OrderItem> items = entity.getItems() == null ? List.of() : entity.getItems().stream()
                .map(item -> new OrderItem(
                        ProductId.from(item.getProductId()),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice()))
                .toList();
        return new Order(
                OrderId.from(entity.getId()),
                entity.getExternalId() != null ? new ExternalId(entity.getExternalId()) : null,
                new OrderNumber(entity.getOrderNumber()),
                UserId.from(entity.getUserId()),
                items,
                entity.getTotalAmount(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
