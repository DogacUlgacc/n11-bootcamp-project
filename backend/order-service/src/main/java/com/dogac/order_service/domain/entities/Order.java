package com.dogac.order_service.domain.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.dogac.order_service.domain.core.AggregateRoot;
import com.dogac.order_service.domain.enums.OrderStatus;
import com.dogac.order_service.domain.valueobjects.ExternalId;
import com.dogac.order_service.domain.valueobjects.OrderItem;
import com.dogac.order_service.domain.valueobjects.OrderId;
import com.dogac.order_service.domain.valueobjects.OrderNumber;
import com.dogac.order_service.domain.valueobjects.UserId;

public class Order implements AggregateRoot<OrderId> {
    private final OrderId id;
    private final ExternalId externalId;
    private final OrderNumber orderNumber;
    private final UserId userId;
    private final List<OrderItem> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    public Order(OrderId id, ExternalId externalId, OrderNumber orderNumber, UserId userId, List<OrderItem> items,
            BigDecimal totalAmount, OrderStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.externalId = externalId;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.items = items == null ? List.of() : List.copyOf(items);
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        recalculateTotalAmount();
    }

    @Override
    public OrderId getId() {
        return id;
    }

    public ExternalId getExternalId() {
        return externalId;
    }

    public OrderNumber getOrderNumber() {
        return orderNumber;
    }

    public UserId getUserId() {
        return userId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void confirm() {
        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    private void recalculateTotalAmount() {
        BigDecimal calculated = items.stream()
                .map(OrderItem::totalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalAmount = calculated;
    }
}
