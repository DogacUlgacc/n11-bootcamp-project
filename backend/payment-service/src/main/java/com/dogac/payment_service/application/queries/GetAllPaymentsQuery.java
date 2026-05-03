package com.dogac.payment_service.application.queries;

import java.util.List;

import com.dogac.payment_service.application.core.Query;
import com.dogac.payment_service.application.dto.PaymentResponse;

public record GetAllPaymentsQuery() implements Query<List<PaymentResponse>> {
}
