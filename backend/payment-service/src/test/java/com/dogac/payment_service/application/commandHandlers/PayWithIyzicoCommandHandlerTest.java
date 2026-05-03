package com.dogac.payment_service.application.commandHandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.dogac.common_events.event.PaymentFailedEvent;
import com.dogac.common_events.event.PaymentSucceededEvent;
import com.dogac.payment_service.application.commands.PayWithIyzicoCommand;
import com.dogac.payment_service.application.dto.PaymentResponse;
import com.dogac.payment_service.application.mapper.PaymentResponseMapper;
import com.dogac.payment_service.application.port.CardInfo;
import com.dogac.payment_service.application.port.PaymentProviderClient;
import com.dogac.payment_service.application.port.ProviderPaymentResult;
import com.dogac.payment_service.domain.entities.Payment;
import com.dogac.payment_service.domain.enums.PaymentStatus;
import com.dogac.payment_service.domain.repositories.PaymentRepository;
import com.dogac.payment_service.domain.valueobjects.Money;
import com.dogac.payment_service.domain.valueobjects.OrderId;
import com.dogac.payment_service.domain.valueobjects.PaymentId;
import com.dogac.payment_service.infrastructure.kafka.publisher.KafkaEventPublisher;

@ExtendWith(MockitoExtension.class)
class PayWithIyzicoCommandHandlerTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentProviderClient paymentProviderClient;

    @Test
    void shouldCompletePaymentWhenIyzicoReturnsSuccess() {
        TestKafkaEventPublisher kafkaEventPublisher = new TestKafkaEventPublisher();
        PayWithIyzicoCommandHandler handler = newHandler(kafkaEventPublisher);
        Payment payment = Payment.create(OrderId.from(java.util.UUID.randomUUID()),
                Money.of(BigDecimal.valueOf(250), "TRY"));
        PayWithIyzicoCommand command = commandFor(payment);
        when(paymentRepository.findById(PaymentId.from(command.paymentId()))).thenReturn(Optional.of(payment));
        when(paymentProviderClient.pay(any(Payment.class), any(CardInfo.class)))
                .thenReturn(new ProviderPaymentResult(true, "iyzico-123", null));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = handler.handle(command);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();
        assertEquals(PaymentStatus.COMPLETED, savedPayment.getStatus());
        assertEquals("iyzico-123", savedPayment.getProviderPaymentId().value());
        assertEquals(PaymentStatus.COMPLETED, response.status());

        assertEquals(savedPayment.getId().value(), kafkaEventPublisher.paymentSucceededEvent.paymentId());
        assertEquals(savedPayment.getOrderId().value(), kafkaEventPublisher.paymentSucceededEvent.orderId());
        assertEquals(null, kafkaEventPublisher.paymentFailedEvent);
    }

    @Test
    void shouldFailPaymentWhenIyzicoReturnsFail() {
        TestKafkaEventPublisher kafkaEventPublisher = new TestKafkaEventPublisher();
        PayWithIyzicoCommandHandler handler = newHandler(kafkaEventPublisher);
        Payment payment = Payment.create(OrderId.from(java.util.UUID.randomUUID()),
                Money.of(BigDecimal.valueOf(250), "TRY"));
        PayWithIyzicoCommand command = commandFor(payment);
        when(paymentRepository.findById(PaymentId.from(command.paymentId()))).thenReturn(Optional.of(payment));
        when(paymentProviderClient.pay(any(Payment.class), any(CardInfo.class)))
                .thenReturn(new ProviderPaymentResult(false, null, "payment declined"));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = handler.handle(command);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();
        assertEquals(PaymentStatus.FAILED, savedPayment.getStatus());
        assertEquals(PaymentStatus.FAILED, response.status());

        assertEquals(savedPayment.getId().value(), kafkaEventPublisher.paymentFailedEvent.paymentId());
        assertEquals(savedPayment.getOrderId().value(), kafkaEventPublisher.paymentFailedEvent.orderId());
        assertEquals("payment declined", kafkaEventPublisher.paymentFailedEvent.reason());
        assertEquals(null, kafkaEventPublisher.paymentSucceededEvent);
    }

    private PayWithIyzicoCommandHandler newHandler(KafkaEventPublisher kafkaEventPublisher) {
        return new PayWithIyzicoCommandHandler(
                paymentRepository,
                paymentProviderClient,
                new PaymentResponseMapper(),
                kafkaEventPublisher);
    }

    private PayWithIyzicoCommand commandFor(Payment payment) {
        return new PayWithIyzicoCommand(
                payment.getId().value(),
                "Dogac Test",
                "5528790000000008",
                "12",
                "2030",
                "123");
    }

    private static class TestKafkaEventPublisher extends KafkaEventPublisher {
        private PaymentSucceededEvent paymentSucceededEvent;
        private PaymentFailedEvent paymentFailedEvent;

        TestKafkaEventPublisher() {
            super((KafkaTemplate<String, Object>) null);
        }

        @Override
        public void publishPaymentSucceeded(PaymentSucceededEvent event) {
            this.paymentSucceededEvent = event;
        }

        @Override
        public void publishPaymentFailed(PaymentFailedEvent event) {
            this.paymentFailedEvent = event;
        }
    }
}
