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
import com.dogac.payment_service.infrastructure.kafka.publisher.KafkaEventPublisher;

@Component
public class CompletePaymentCommandHandler implements CommandHandler<CompletePaymentCommand, PaymentResponse> {

    private final KafkaEventPublisher kafkaEventPublisher;
    private final PaymentRepository paymentRepository;
    private final PaymentResponseMapper mapper;

    public CompletePaymentCommandHandler(KafkaEventPublisher kafkaEventPublisher, PaymentRepository paymentRepository,
            PaymentResponseMapper mapper) {
        this.kafkaEventPublisher = kafkaEventPublisher;
        this.paymentRepository = paymentRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public PaymentResponse handle(CompletePaymentCommand command) {
        Payment payment = paymentRepository.findById(PaymentId.from(command.paymentId()))
                .orElseThrow(() -> new PaymentNotFoundException("PaymentNotFound"));

        payment.complete(command.providerPaymentId());
        Payment saved = paymentRepository.save(payment);
        kafkaEventPublisher.publishPaymentSucceeded(
                new PaymentSucceededEvent(saved.getId().value(), saved.getOrderId().value(), saved.getOrderId().value(),
                        saved.getMoney().amount(), saved.getMoney().currency().toString()));
        return mapper.toResponse(saved);
    }

}
