package com.dogac.order_service.application.commandHandlers;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dogac.order_service.application.commands.DeleteOrderCommand;
import com.dogac.order_service.application.core.CommandHandler;
import com.dogac.order_service.domain.repositories.OrderRepository;
import com.dogac.order_service.domain.valueobjects.OrderId;

@Component
@Transactional
public class DeleteOrderCommandHandler implements CommandHandler<DeleteOrderCommand, Void> {
    private final OrderRepository orderRepository;

    public DeleteOrderCommandHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Void handle(DeleteOrderCommand command) {
        orderRepository.deleteById(OrderId.from(command.id()));
        return null;
    }
}
