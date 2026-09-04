package com.dogac.payment_service.application.commandHandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import com.dogac.payment_service.infrastructure.outbox.OutboxEventService;

@ExtendWith(MockitoExtension.class)
class PayWithIyzicoCommandHandlerTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentProviderClient paymentProviderClient;

    @Mock
    private OutboxEventService outboxEventService;

    @Test
    void shouldCompletePaymentWhenIyzicoReturnsSuccess() {
        PayWithIyzicoCommandHandler handler = newHandler();
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

        ArgumentCaptor<PaymentSucceededEvent> eventCaptor = ArgumentCaptor.forClass(PaymentSucceededEvent.class);
        verify(outboxEventService).savePaymentSucceededEvent(eventCaptor.capture());
        PaymentSucceededEvent succeededEvent = eventCaptor.getValue();
        assertEquals(savedPayment.getId().value(), succeededEvent.paymentId());
        assertEquals(savedPayment.getOrderId().value(), succeededEvent.orderId());
        verify(outboxEventService, never()).savePaymentFailedEvent(any(PaymentFailedEvent.class));
    }

    @Test
    void shouldFailPaymentWhenIyzicoReturnsFail() {
        PayWithIyzicoCommandHandler handler = newHandler();
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

        ArgumentCaptor<PaymentFailedEvent> eventCaptor = ArgumentCaptor.forClass(PaymentFailedEvent.class);
        verify(outboxEventService).savePaymentFailedEvent(eventCaptor.capture());
        PaymentFailedEvent failedEvent = eventCaptor.getValue();
        assertEquals(savedPayment.getId().value(), failedEvent.paymentId());
        assertEquals(savedPayment.getOrderId().value(), failedEvent.orderId());
        assertEquals("payment declined", failedEvent.reason());
        verify(outboxEventService, never()).savePaymentSucceededEvent(any(PaymentSucceededEvent.class));
    }

    private PayWithIyzicoCommandHandler newHandler() {
        return new PayWithIyzicoCommandHandler(
                paymentRepository,
                paymentProviderClient,
                new PaymentResponseMapper(),
                outboxEventService);
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
}
