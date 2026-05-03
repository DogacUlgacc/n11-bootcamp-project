package com.dogac.order_service.domain.services;

import com.dogac.order_service.domain.entities.Order;
import com.dogac.order_service.domain.exceptions.DuplicateOrderNumberException;
import com.dogac.order_service.domain.repositories.OrderRepository;
import com.dogac.order_service.domain.valueobjects.OrderNumber;

public class OrderDomainService {
    private final OrderRepository orderRepository;

    public OrderDomainService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void ensureOrderNumberIsUnique(OrderNumber orderNumber) {
        if (orderRepository.existsByOrderNumber(orderNumber)) {
            throw new DuplicateOrderNumberException("Order number already exists: " + orderNumber.value());
        }
    }

    public void confirmOrder(Order order) {
        order.confirm();
    }

    public void cancelOrder(Order order) {
        order.cancel();
    }
}
