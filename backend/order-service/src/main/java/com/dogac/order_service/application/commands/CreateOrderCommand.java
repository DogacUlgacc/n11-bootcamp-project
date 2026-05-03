package com.dogac.order_service.application.commands;

import java.util.List;
import java.util.UUID;

import com.dogac.order_service.application.core.Command;
import com.dogac.order_service.application.dto.CreateOrderItemRequest;
import com.dogac.order_service.application.dto.CreatedOrderResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateOrderCommand(
                @JsonProperty("userId") @NotNull UUID userId,
                @JsonProperty("items") @NotEmpty List<@Valid CreateOrderItemRequest> items)
                implements Command<CreatedOrderResponse> {
}
