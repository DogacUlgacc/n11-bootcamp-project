package com.dogac.payment_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.iyzipay.Options;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class IyzicoConfig {

    private final IyzicoProperties properties;

    @Bean
    public Options iyzicoOptions() {
        Options options = new Options();
        options.setApiKey(properties.apiKey());
        options.setSecretKey(properties.secretKey());
        options.setBaseUrl(properties.baseUrl());
        return options;
    }
}