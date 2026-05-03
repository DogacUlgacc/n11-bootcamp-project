package com.dogac.payment_service.application.queryHandlers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dogac.payment_service.application.core.QueryHandler;
import com.dogac.payment_service.application.dto.PaymentResponse;
import com.dogac.payment_service.application.mapper.PaymentResponseMapper;
import com.dogac.payment_service.application.queries.GetAllPaymentsQuery;
import com.dogac.payment_service.domain.repositories.PaymentRepository;

@Component
public class GetAllPaymentsQueryHandler implements QueryHandler<GetAllPaymentsQuery, List<PaymentResponse>> {

    private final PaymentRepository paymentRepository;
    private final PaymentResponseMapper paymentResponseMapper;

    public GetAllPaymentsQueryHandler(PaymentRepository paymentRepository, PaymentResponseMapper paymentResponseMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentResponseMapper = paymentResponseMapper;
    }

    @Override
    public List<PaymentResponse> handle(GetAllPaymentsQuery query) {
        return paymentRepository.findAll().stream()
                .map(paymentResponseMapper::toResponse)
                .toList();
    }
}
