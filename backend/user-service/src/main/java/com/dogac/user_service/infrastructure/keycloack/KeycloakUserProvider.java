package com.dogac.user_service.infrastructure.keycloack;

public interface KeycloakUserProvider {
    String createUser(
            String email,
            String password,
            String firstName,
            String lastName);
}