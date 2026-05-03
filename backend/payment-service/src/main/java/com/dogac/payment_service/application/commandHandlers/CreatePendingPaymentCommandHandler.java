package com.dogac.payment_service.application.commandHandlers;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dogac.payment_service.application.commands.CreatePendingPaymentCommand;
import com.dogac.payment_service.application.core.CommandHandler;
import com.dogac.payment_service.application.dto.CreatedPaymentResponse;
import com.dogac.payment_service.application.mapper.CreatePaymentMapper;
import com.dogac.payment_service.domain.entities.Payment;
import com.dogac.payment_service.domain.repositories.PaymentRepository;
import com.dogac.payment_service.domain.services.PaymentDomainService;
import com.dogac.payment_service.domain.valueobjects.OrderId;

@Component
@Transactional
public class CreatePendingPaymentCommandHandler
        implements CommandHandler<CreatePendingPaymentCommand, CreatedPaymentResponse> {

    private final CreatePaymentMapper createPaymentMapper;
    private final PaymentRepository paymentRepository;
    private final PaymentDomainService paymentDomainService;

    public CreatePendingPaymentCommandHandler(
            CreatePaymentMapper createPaymentMapper,
            PaymentRepository paymentRepository,
            PaymentDomainService paymentDomainService) {
        this.createPaymentMapper = createPaymentMapper;
        this.paymentRepository = paymentRepository;
        this.paymentDomainService = paymentDomainService;
    }

    @Override
    public CreatedPaymentResponse handle(CreatePendingPaymentCommand command) {
        paymentDomainService.ensureOrderHasNoPayment(OrderId.from(command.orderId()));

        Payment payment = createPaymentMapper.toEntity(command);
        payment = paymentRepository.save(payment);

        return createPaymentMapper.toResponse(payment);
    }
}
