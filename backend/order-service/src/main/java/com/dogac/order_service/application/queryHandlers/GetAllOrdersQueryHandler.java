package com.dogac.order_service.application.queryHandlers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dogac.order_service.application.core.QueryHandler;
import com.dogac.order_service.application.dto.OrderResponse;
import com.dogac.order_service.application.mapper.OrderResponseMapper;
import com.dogac.order_service.application.queries.GetAllOrdersQuery;
import com.dogac.order_service.domain.repositories.OrderRepository;

@Component
public class GetAllOrdersQueryHandler implements QueryHandler<GetAllOrdersQuery, List<OrderResponse>> {
    private final OrderResponseMapper orderResponseMapper;
    private final OrderRepository orderRepository;

    public GetAllOrdersQueryHandler(OrderResponseMapper orderResponseMapper, OrderRepository orderRepository) {
        this.orderResponseMapper = orderResponseMapper;
        this.orderRepository = orderRepository;
    }

    @Override
    public List<OrderResponse> handle(GetAllOrdersQuery query) {
        return orderRepository.findAll().stream().map(orderResponseMapper::toResponse).toList();
    }
}
