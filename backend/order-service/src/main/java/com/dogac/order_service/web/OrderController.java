package com.dogac.order_service.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dogac.order_service.application.bus.CommandBus;
import com.dogac.order_service.application.bus.QueryBus;
import com.dogac.order_service.application.commands.CreateCheckoutCommand;
import com.dogac.order_service.application.commands.CreateOrderCommand;
import com.dogac.order_service.application.commands.DeleteOrderCommand;
import com.dogac.order_service.application.commands.UpdateOrderCommand;
import com.dogac.order_service.application.dto.CreatedOrderResponse;
import com.dogac.order_service.application.dto.OrderResponse;
import com.dogac.order_service.application.dto.UpdateOrderRequest;
import com.dogac.order_service.application.dto.UpdatedOrderResponse;
import com.dogac.order_service.application.feignDto.UserDto;
import com.dogac.order_service.application.queries.GetAllOrdersQuery;
import com.dogac.order_service.application.queries.GetOrderByIdQuery;
import com.dogac.order_service.infrastructure.feignclients.UserClient;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/orders")
@Slf4j
public class OrderController {
    private final CommandBus commandBus;
    private final QueryBus queryBus;
    private final UserClient userClient;

    public OrderController(CommandBus commandBus, QueryBus queryBus, UserClient userClient) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
        this.userClient = userClient;
    }

    /* manuel post order method */
    @PostMapping
    public ResponseEntity<CreatedOrderResponse> createOrder(@RequestBody @Valid CreateOrderCommand command) {
        CreatedOrderResponse response = commandBus.send(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/checkout")
    public ResponseEntity<CreatedOrderResponse> checkoutOrder(
            @RequestHeader("X-External-Id") String externalId,
            @RequestBody @Valid CreateCheckoutCommand request) {

        log.info("ExternalId: " + externalId);
        log.info("CartId: " + request.cartId());
        UserDto user = userClient.getUserByExternalId(externalId);
        log.info("UserId: " + user.id());
        CreateCheckoutCommand command = new CreateCheckoutCommand(
                user.id(),
                request.cartId());

        CreatedOrderResponse response = commandBus.send(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdatedOrderResponse> updateOrder(@PathVariable UUID id,
            @RequestBody @Valid UpdateOrderRequest request) {
        UpdatedOrderResponse response = commandBus.send(new UpdateOrderCommand(id, request.status()));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        commandBus.send(new DeleteOrderCommand(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id) {
        OrderResponse response = queryBus.execute(new GetOrderByIdQuery(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders() {
        List<OrderResponse> responseList = queryBus.execute(new GetAllOrdersQuery());
        return ResponseEntity.ok(responseList);
    }
}
