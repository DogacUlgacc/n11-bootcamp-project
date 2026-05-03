package com.dogac.order_service.application.queries;

import java.util.UUID;

import com.dogac.order_service.application.core.Query;
import com.dogac.order_service.application.dto.OrderResponse;

import jakarta.validation.constraints.NotNull;

public record GetOrderByIdQuery(@NotNull UUID id) implements Query<OrderResponse> {
}
