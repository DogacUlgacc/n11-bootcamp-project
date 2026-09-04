package com.dogac.cart_service.infrastructure.resilience.user;

import org.springframework.stereotype.Service;

import com.dogac.cart_service.application.dto.feignDto.UserDto;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserRetryService {

    private final UserCircuitBreakerService userCircuitBreakerService;

    public UserRetryService(UserCircuitBreakerService userCircuitBreakerService) {
        this.userCircuitBreakerService = userCircuitBreakerService;
    }

    @Retry(name = "userService")
    public UserDto getUserByExternalId(String id) {
        log.info("Calling User Service: {}", id);
        return userCircuitBreakerService
                .getUserByExternalId(id);
    }
}
