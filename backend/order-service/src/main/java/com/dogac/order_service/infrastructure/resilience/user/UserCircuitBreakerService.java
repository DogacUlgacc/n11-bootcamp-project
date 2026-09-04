package com.dogac.order_service.infrastructure.resilience.user;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dogac.order_service.application.feignDto.UserDto;
import com.dogac.order_service.infrastructure.feignclients.UserClient;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class UserCircuitBreakerService {

    private final UserClient userClient;

    public UserCircuitBreakerService(UserClient userClient) {
        this.userClient = userClient;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByExternalIdFallback")
    public UserDto getUserByExternalId(String id) {
        return userClient.getUserByExternalId(id);
    }

    public UserDto getUserByExternalIdFallback(
            String id,
            Throwable throwable) {

        if (throwable instanceof CallNotPermittedException) {
            throw new RuntimeException(
                    "User service is currently unavailable",
                    throwable);
        }

        sneakyThrow(throwable);
        return null;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByIdFallback")
    public UserDto getUserById(UUID id) {
        return userClient.getUserById(id);
    }

    public UserDto getUserByIdFallback(
            UUID id,
            Throwable throwable) {

        if (throwable instanceof CallNotPermittedException) {
            throw new RuntimeException(
                    "User service is currently unavailable",
                    throwable);
        }

        sneakyThrow(throwable);
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void sneakyThrow(
            Throwable throwable) throws T {

        throw (T) throwable;
    }

}
