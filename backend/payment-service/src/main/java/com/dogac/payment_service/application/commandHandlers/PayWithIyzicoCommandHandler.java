package com.dogac.payment_service.application.commandHandlers;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dogac.common_events.event.PaymentFailedEvent;
import com.dogac.common_events.event.PaymentSucceededEvent;
import com.dogac.payment_service.application.commands.PayWithIyzicoCommand;
import com.dogac.payment_service.application.core.CommandHandler;
import com.dogac.payment_service.application.dto.PaymentResponse;
import com.dogac.payment_service.application.mapper.PaymentResponseMapper;
import com.dogac.payment_service.application.port.CardInfo;
import com.dogac.payment_service.application.port.PaymentProviderClient;
import com.dogac.payment_service.application.port.ProviderPaymentResult;
import com.dogac.payment_service.domain.entities.Payment;
import com.dogac.payment_service.domain.exceptions.PaymentNotFoundException;
import com.dogac.payment_service.domain.repositories.PaymentRepository;
import com.dogac.payment_service.domain.valueobjects.PaymentId;
import com.dogac.payment_service.infrastructure.kafka.publisher.KafkaEventPublisher;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PayWithIyzicoCommandHandler implements CommandHandler<PayWithIyzicoCommand, PaymentResponse> {

    private final PaymentRepository paymentRepository;
    private final PaymentProviderClient paymentProviderClient;
    private final PaymentResponseMapper paymentMapper;
    private final KafkaEventPublisher kafkaEventPublisher;

    public PayWithIyzicoCommandHandler(PaymentRepository paymentRepository, PaymentProviderClient paymentProviderClient,
            PaymentResponseMapper paymentMapper,
            com.dogac.payment_service.infrastructure.kafka.publisher.KafkaEventPublisher kafkaEventPublisher) {
        this.paymentRepository = paymentRepository;
        this.paymentProviderClient = paymentProviderClient;
        this.paymentMapper = paymentMapper;
        this.kafkaEventPublisher = kafkaEventPublisher;
    }

    @Override
    @Transactional
    public PaymentResponse handle(PayWithIyzicoCommand command) {

        log.info("PayWithIyzicoCommand started. paymentId={}", command.paymentId());

        Payment payment = paymentRepository.findById(
                PaymentId.from(command.paymentId()))
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        ProviderPaymentResult result = paymentProviderClient.pay(
                payment,
                new CardInfo(
                        command.cardHolderName(),
                        command.cardNumber(),
                        command.expireMonth(),
                        command.expireYear(),
                        command.cvc()));

        if (result.success()) {
            payment.complete(result.providerPaymentId());

            Payment saved = paymentRepository.save(payment);

            kafkaEventPublisher.publishPaymentSucceeded(
                    new PaymentSucceededEvent(
                            saved.getId().value(),
                            saved.getOrderId().value(),
                            saved.getOrderId().value(),
                            saved.getMoney().amount(),
                            saved.getMoney().currency().getCurrencyCode()));

            log.info("Payment completed with Iyzico. paymentId={}, providerPaymentId={}",
                    saved.getId().value(),
                    result.providerPaymentId());

            return paymentMapper.toResponse(saved);
        }

        payment.fail(result.errorMessage());

        Payment saved = paymentRepository.save(payment);

        kafkaEventPublisher.publishPaymentFailed(
                new PaymentFailedEvent(
                        saved.getId().value(),
                        saved.getOrderId().value(),
                        result.errorMessage()));

        log.warn("Payment failed with Iyzico. paymentId={}, reason={}",
                saved.getId().value(),
                result.errorMessage());

        return paymentMapper.toResponse(saved);
    }
}
