package com.dogac.order_service.application.commandHandlers;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dogac.order_service.application.commands.UpdateOrderCommand;
import com.dogac.order_service.application.core.CommandHandler;
import com.dogac.order_service.application.dto.UpdatedOrderResponse;
import com.dogac.order_service.application.mapper.UpdateOrderMapper;
import com.dogac.order_service.domain.entities.Order;
import com.dogac.order_service.domain.exceptions.OrderNotFoundException;
import com.dogac.order_service.domain.repositories.OrderRepository;
import com.dogac.order_service.domain.valueobjects.OrderId;

@Component
@Transactional
public class UpdateOrderCommandHandler implements CommandHandler<UpdateOrderCommand, UpdatedOrderResponse> {
    private final UpdateOrderMapper updateOrderMapper;
    private final OrderRepository orderRepository;

    public UpdateOrderCommandHandler(UpdateOrderMapper updateOrderMapper, OrderRepository orderRepository) {
        this.updateOrderMapper = updateOrderMapper;
        this.orderRepository = orderRepository;
    }

    @Override
    public UpdatedOrderResponse handle(UpdateOrderCommand command) {
        Order order = orderRepository.findById(OrderId.from(command.id()))
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + command.id()));
        switch (command.status()) {
            case CONFIRMED -> order.confirm();
            case CANCELLED -> order.cancel();
            default -> {
                // Placeholder transition for CREATED status.
            }
        }
        orderRepository.save(order);
        return updateOrderMapper.toResponse(order);
    }
}
