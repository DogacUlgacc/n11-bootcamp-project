package com.dogac.payment_service.application.port;

import com.dogac.payment_service.domain.entities.Payment;

public interface PaymentProviderClient {
    ProviderPaymentResult pay(Payment payment, CardInfo cardInfo);
}
