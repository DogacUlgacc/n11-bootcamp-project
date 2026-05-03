package com.dogac.payment_service.application.queries;

import java.util.UUID;

import com.dogac.payment_service.application.core.Query;
import com.dogac.payment_service.application.dto.PaymentResponse;

public record GetPaymentByIdQuery(UUID id) implements Query<PaymentResponse> {
}
