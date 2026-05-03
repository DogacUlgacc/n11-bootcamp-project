package com.dogac.payment_service.application.commandHandlers;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dogac.common_events.event.PaymentFailedEvent;
import com.dogac.payment_service.application.commands.FailPaymentCommand;
import com.dogac.payment_service.application.core.CommandHandler;
import com.dogac.payment_service.application.dto.PaymentResponse;
import com.dogac.payment_service.application.mapper.PaymentResponseMapper;
import com.dogac.payment_service.domain.entities.Payment;
import com.dogac.payment_service.domain.exceptions.PaymentNotFoundException;
import com.dogac.payment_service.domain.repositories.PaymentRepository;
import com.dogac.payment_service.domain.valueobjects.PaymentId;
import com.dogac.payment_service.infrastructure.kafka.publisher.KafkaEventPublisher;

@Component
public class FailPaymentCommandHandler
        implements CommandHandler<FailPaymentCommand, PaymentResponse> {

    private final PaymentRepository paymentRepository;
    private final PaymentResponseMapper mapper;
    private final KafkaEventPublisher kafkaEventPublisher;

    public FailPaymentCommandHandler(PaymentRepository paymentRepository, PaymentResponseMapper mapper,
            KafkaEventPublisher kafkaEventPublisher) {
        this.paymentRepository = paymentRepository;
        this.mapper = mapper;
        this.kafkaEventPublisher = kafkaEventPublisher;
    }

    @Override
    @Transactional
    public PaymentResponse handle(FailPaymentCommand command) {
        Payment payment = paymentRepository.findById(PaymentId.from(command.paymentId()))
                .orElseThrow(() -> new PaymentNotFoundException("PaymentNotFound"));

        payment.fail(command.reason());
        Payment saved = paymentRepository.save(payment);
        kafkaEventPublisher.publishPaymentFailed(new PaymentFailedEvent(
                saved.getId().value(),
                saved.getOrderId().value(),
                command.reason()));
        return mapper.toResponse(saved);
    }

}
