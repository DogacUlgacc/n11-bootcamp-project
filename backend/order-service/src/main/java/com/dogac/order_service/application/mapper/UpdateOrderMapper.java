package com.dogac.order_service.application.mapper;

import org.springframework.stereotype.Component;

import com.dogac.order_service.application.dto.UpdatedOrderResponse;
import com.dogac.order_service.domain.entities.Order;

@Component
public class UpdateOrderMapper {
    public UpdatedOrderResponse toResponse(Order order) {
        return new UpdatedOrderResponse(order.getId().value(), order.getStatus());
    }
}
