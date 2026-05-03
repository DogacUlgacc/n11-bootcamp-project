package com.dogac.order_service.application.queries;

import java.util.List;

import com.dogac.order_service.application.core.Query;
import com.dogac.order_service.application.dto.OrderResponse;

public record GetAllOrdersQuery() implements Query<List<OrderResponse>> {
}
