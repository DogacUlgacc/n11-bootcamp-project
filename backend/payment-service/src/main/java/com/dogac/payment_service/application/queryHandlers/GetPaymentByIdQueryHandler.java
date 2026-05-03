package com.dogac.payment_service.application.queryHandlers;

import org.springframework.stereotype.Component;

import com.dogac.payment_service.application.core.QueryHandler;
import com.dogac.payment_service.application.dto.PaymentResponse;
import com.dogac.payment_service.application.mapper.PaymentResponseMapper;
import com.dogac.payment_service.application.queries.GetPaymentByIdQuery;
import com.dogac.payment_service.domain.entities.Payment;
import com.dogac.payment_service.domain.exceptions.PaymentNotFoundException;
import com.dogac.payment_service.domain.repositories.PaymentRepository;
import com.dogac.payment_service.domain.valueobjects.PaymentId;

@Component
public class GetPaymentByIdQueryHandler implements QueryHandler<GetPaymentByIdQuery, PaymentResponse> {

    private final PaymentRepository paymentRepository;
    private final PaymentResponseMapper paymentResponseMapper;

    public GetPaymentByIdQueryHandler(PaymentRepository paymentRepository, PaymentResponseMapper paymentResponseMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentResponseMapper = paymentResponseMapper;
    }

    @Override
    public PaymentResponse handle(GetPaymentByIdQuery query) {
        Payment payment = paymentRepository.findById(PaymentId.from(query.id()))
                .orElseThrow(() -> new PaymentNotFoundException("Payment with given id is not found"));

        return paymentResponseMapper.toResponse(payment);
    }
}
