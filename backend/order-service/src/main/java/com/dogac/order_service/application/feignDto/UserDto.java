package com.dogac.order_service.application.feignDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserDto(
        UUID id,
        UUID externalId,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String userType,
        String status,
        List<AddressDto> addresses,
        Instant createdAt,
        Instant updatedAt) {
}