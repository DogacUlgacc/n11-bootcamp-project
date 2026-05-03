package com.dogac.user_service.application.queries;

import com.dogac.user_service.application.core.Query;
import com.dogac.user_service.application.dto.UserIdentityResponse;

import jakarta.validation.constraints.NotBlank;

public record GetUserByExternalIdQuery(@NotBlank String externalId) implements Query<UserIdentityResponse> {
}
