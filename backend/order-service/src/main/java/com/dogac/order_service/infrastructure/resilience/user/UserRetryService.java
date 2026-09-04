package com.dogac.order_service.infrastructure.resilience.user;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dogac.order_service.application.feignDto.UserDto;

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
        log.info("Calling User Service getUserByExternalId: {}", id);
        return userCircuitBreakerService
                .getUserByExternalId(id);
    }

    @Retry(name = "userService")
    public UserDto getUserById(UUID id) {
        log.info("Calling User Service getUserById: {}", id);
        return userCircuitBreakerService
                .getUserById(id);
    }
}
