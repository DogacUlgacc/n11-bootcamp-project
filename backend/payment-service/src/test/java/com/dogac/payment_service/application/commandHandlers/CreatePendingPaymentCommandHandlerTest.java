package com.dogac.payment_service.application.commandHandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dogac.payment_service.application.commands.CreatePendingPaymentCommand;
import com.dogac.payment_service.application.dto.CreatedPaymentResponse;
import com.dogac.payment_service.application.mapper.CreatePaymentMapper;
import com.dogac.payment_service.domain.entities.Payment;
import com.dogac.payment_service.domain.enums.PaymentProvider;
import com.dogac.payment_service.domain.enums.PaymentStatus;
import com.dogac.payment_service.domain.repositories.PaymentRepository;
import com.dogac.payment_service.domain.services.PaymentDomainService;

@ExtendWith(MockitoExtension.class)
class CreatePendingPaymentCommandHandlerTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Test
    void shouldCreatePendingPayment() {
        CreatePendingPaymentCommandHandler handler = new CreatePendingPaymentCommandHandler(
                new CreatePaymentMapper(),
                paymentRepository,
                new PaymentDomainService(paymentRepository));
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CreatePendingPaymentCommand command = new CreatePendingPaymentCommand(
                orderId,
                userId,
                BigDecimal.valueOf(150),
                "TRY");
        when(paymentRepository.existsByOrderId(any())).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreatedPaymentResponse response = handler.handle(command);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();
        assertNotNull(savedPayment.getId());
        assertEquals(orderId, savedPayment.getOrderId().value());
        assertEquals(PaymentProvider.IYZICO, savedPayment.getPaymentProvider());
        assertEquals(PaymentStatus.PENDING, savedPayment.getStatus());
        assertEquals(BigDecimal.valueOf(150), savedPayment.getMoney().amount());
        assertEquals("TRY", savedPayment.getMoney().currency().getCurrencyCode());

        assertEquals(savedPayment.getId().value(), response.id());
        assertEquals(orderId, response.orderId());
        assertEquals(PaymentStatus.PENDING, response.status());
    }
}
