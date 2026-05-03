package com.dogac.order_service.application.commands;

import java.util.UUID;

import com.dogac.order_service.application.core.Command;
import com.dogac.order_service.application.dto.CreatedOrderResponse;

public record CreateCheckoutCommand(
                UUID userId,
                UUID cartId) implements Command<CreatedOrderResponse> {

}
