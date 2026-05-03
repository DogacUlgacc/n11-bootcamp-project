package com.dogac.gateway_server;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class UserHeaderFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .map(auth -> {
                    Jwt jwt = auth.getToken();

                    String userId = jwt.getSubject();
                    String username = jwt.getClaimAsString("preferred_username");
                    String email = jwt.getClaimAsString("email");

                    ServerHttpRequest request = exchange.getRequest()
                            .mutate()
                            .header("X-External-Id", userId)
                            .header("X-User-Id", userId)
                            .header("X-Username", username)
                            .header("X-Email", email)
                            .build();

                    return exchange.mutate().request(request).build();
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }
}
