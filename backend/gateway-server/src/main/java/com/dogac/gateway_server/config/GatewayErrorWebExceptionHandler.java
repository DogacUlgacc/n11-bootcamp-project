package com.dogac.gateway_server.config;

import java.net.ConnectException;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GatewayErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(
            ServerWebExchange exchange,
            Throwable ex) {

        Throwable cause = ex;

        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        if (cause instanceof ConnectException) {

            exchange.getResponse()
                    .setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);

            return exchange.getResponse()
                    .setComplete();
        }

        return Mono.error(ex);
    }
}