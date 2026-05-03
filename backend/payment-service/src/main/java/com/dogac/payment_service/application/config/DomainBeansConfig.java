package com.dogac.payment_service.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dogac.payment_service.domain.repositories.PaymentRepository;
import com.dogac.payment_service.domain.services.PaymentDomainService;

@Configuration
public class DomainBeansConfig {

    @Bean
    public PaymentDomainService paymentDomainService(PaymentRepository paymentRepository) {
        return new PaymentDomainService(paymentRepository);
    }
}
