package com.dogac.order_service.application.queryHandlers;

import org.springframework.stereotype.Component;

import com.dogac.order_service.application.core.QueryHandler;
import com.dogac.order_service.application.dto.OrderResponse;
import com.dogac.order_service.application.mapper.OrderResponseMapper;
import com.dogac.order_service.application.queries.GetOrderByIdQuery;
import com.dogac.order_service.domain.entities.Order;
import com.dogac.order_service.domain.exceptions.OrderNotFoundException;
import com.dogac.order_service.domain.repositories.OrderRepository;
import com.dogac.order_service.domain.valueobjects.OrderId;

@Component
public class GetOrderByIdQueryHandler implements QueryHandler<GetOrderByIdQuery, OrderResponse> {
    private final OrderResponseMapper orderResponseMapper;
    private final OrderRepository orderRepository;

    public GetOrderByIdQueryHandler(OrderResponseMapper orderResponseMapper, OrderRepository orderRepository) {
        this.orderResponseMapper = orderResponseMapper;
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderResponse handle(GetOrderByIdQuery query) {
        Order order = orderRepository.findById(OrderId.from(query.id()))
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + query.id()));
        return orderResponseMapper.toResponse(order);
    }
}
