package com.dogac.order_service.infrastructure.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.dogac.order_service.domain.entities.Order;
import com.dogac.order_service.domain.repositories.OrderRepository;
import com.dogac.order_service.domain.valueobjects.OrderId;
import com.dogac.order_service.domain.valueobjects.OrderNumber;
import com.dogac.order_service.infrastructure.mapper.OrderEntityMapper;
import com.dogac.order_service.infrastructure.repositories.SpringDataOrderRepository;

@Component
public class OrderRepositoryAdapter implements OrderRepository {
    private final SpringDataOrderRepository springDataOrderRepository;
    private final OrderEntityMapper orderEntityMapper;

    public OrderRepositoryAdapter(SpringDataOrderRepository springDataOrderRepository, OrderEntityMapper orderEntityMapper) {
        this.springDataOrderRepository = springDataOrderRepository;
        this.orderEntityMapper = orderEntityMapper;
    }

    @Override
    public Order save(Order order) {
        var entity = orderEntityMapper.toEntity(order);
        entity = springDataOrderRepository.save(entity);
        return orderEntityMapper.toDomain(entity);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return springDataOrderRepository.findById(id.value()).map(orderEntityMapper::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return springDataOrderRepository.findAll().stream().map(orderEntityMapper::toDomain).toList();
    }

    @Override
    public void deleteById(OrderId id) {
        springDataOrderRepository.deleteById(id.value());
    }

    @Override
    public boolean existsById(OrderId id) {
        return springDataOrderRepository.existsById(id.value());
    }

    @Override
    public boolean existsByOrderNumber(OrderNumber orderNumber) {
        return springDataOrderRepository.existsByOrderNumber(orderNumber.value());
    }
}
