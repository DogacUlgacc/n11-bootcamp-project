package com.dogac.user_service.application.dto;

import java.util.UUID;

import com.dogac.user_service.domain.enums.UserStatus;
import com.dogac.user_service.domain.enums.UserType;

public record RegisteredUserResponse(

        UUID userId,
        String externalId,

        String firstName,
        String lastName,

        String email,
        String phoneNumber,

        UserType userType,
        UserStatus status) {
}