package com.dogac.payment_service.application.commandHandlers;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dogac.common_events.event.PaymentSucceededEvent;
import com.dogac.payment_service.application.commands.CompletePaymentCommand;
import com.dogac.payment_service.application.core.CommandHandler;
import com.dogac.payment_service.application.dto.PaymentResponse;
import com.dogac.payment_service.application.mapper.PaymentResponseMapper;
import com.dogac.payment_service.domain.entities.Payment;
import com.dogac.payment_service.domain.exceptions.PaymentNotFoundException;
import com.dogac.payment_service.domain.repositories.PaymentRepository;
import com.dogac.payment_service.domain.valueobjects.PaymentId;
import com.dogac.payment_service.infrastructure.outbox.OutboxEventService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CompletePaymentCommandHandler implements CommandHandler<CompletePaymentCommand, PaymentResponse> {

    private final PaymentRepository paymentRepository;
    private final PaymentResponseMapper mapper;
    private final OutboxEventService outboxEventService;

    public CompletePaymentCommandHandler(PaymentRepository paymentRepository, PaymentResponseMapper mapper,
            OutboxEventService outboxEventService) {
        this.paymentRepository = paymentRepository;
        this.mapper = mapper;
        this.outboxEventService = outboxEventService;
    }

    @Override
    @Transactional
    public PaymentResponse handle(CompletePaymentCommand command) {
        Payment payment = paymentRepository.findById(PaymentId.from(command.paymentId()))
                .orElseThrow(() -> new PaymentNotFoundException("PaymentNotFound"));

        payment.complete(command.providerPaymentId());
        Payment saved = paymentRepository.save(payment);
        log.info("payment saved " + saved);
        PaymentSucceededEvent event = new PaymentSucceededEvent(saved.getId().value(), saved.getOrderId().value(),
                saved.getOrderId().value(),
                saved.getMoney().amount(), saved.getMoney().currency().toString());

        outboxEventService.savePaymentSucceededEvent(event);
        log.info("outbox event service ");
        return mapper.toResponse(saved);
    }
}
