package com.dogac.order_service.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dogac.order_service.domain.repositories.OrderRepository;
import com.dogac.order_service.domain.services.OrderDomainService;

@Configuration
public class DomainBeansConfig {
    @Bean
    public OrderDomainService orderDomainService(OrderRepository orderRepository) {
        return new OrderDomainService(orderRepository);
    }
}
