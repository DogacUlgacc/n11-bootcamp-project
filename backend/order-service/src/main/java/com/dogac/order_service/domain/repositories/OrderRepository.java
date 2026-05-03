package com.dogac.order_service.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.dogac.order_service.domain.entities.Order;
import com.dogac.order_service.domain.valueobjects.OrderId;
import com.dogac.order_service.domain.valueobjects.OrderNumber;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(OrderId id);

    List<Order> findAll();

    void deleteById(OrderId id);

    boolean existsById(OrderId id);

    boolean existsByOrderNumber(OrderNumber orderNumber);
}
