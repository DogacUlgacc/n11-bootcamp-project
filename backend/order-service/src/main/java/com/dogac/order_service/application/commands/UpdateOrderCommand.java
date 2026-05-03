package com.dogac.order_service.application.commands;

import java.util.UUID;

import com.dogac.order_service.application.core.Command;
import com.dogac.order_service.application.dto.UpdatedOrderResponse;
import com.dogac.order_service.domain.enums.OrderStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateOrderCommand(
        @NotNull UUID id,
        @NotNull OrderStatus status) implements Command<UpdatedOrderResponse> {
}
