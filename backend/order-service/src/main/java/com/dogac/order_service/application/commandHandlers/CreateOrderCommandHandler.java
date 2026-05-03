package com.dogac.order_service.application.commandHandlers;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dogac.order_service.application.commands.CreateOrderCommand;
import com.dogac.order_service.application.core.CommandHandler;
import com.dogac.order_service.application.dto.CreatedOrderResponse;
import com.dogac.order_service.application.mapper.CreateOrderMapper;
import com.dogac.order_service.domain.entities.Order;
import com.dogac.order_service.domain.repositories.OrderRepository;
import com.dogac.order_service.domain.services.OrderDomainService;
import com.dogac.order_service.domain.valueobjects.OrderNumber;

@Component
@Transactional
public class CreateOrderCommandHandler implements CommandHandler<CreateOrderCommand, CreatedOrderResponse> {
    private final CreateOrderMapper createOrderMapper;
    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;

    public CreateOrderCommandHandler(CreateOrderMapper createOrderMapper, OrderRepository orderRepository,
            OrderDomainService orderDomainService) {
        this.createOrderMapper = createOrderMapper;
        this.orderRepository = orderRepository;
        this.orderDomainService = orderDomainService;
    }

    @Override
    public CreatedOrderResponse handle(CreateOrderCommand command) {
        OrderNumber orderNumber = OrderNumber.generate();
        orderDomainService.ensureOrderNumberIsUnique(orderNumber);
        Order order = createOrderMapper.toEntity(command, orderNumber);
        orderRepository.save(order);
        return createOrderMapper.toResponse(order);
    }
}
