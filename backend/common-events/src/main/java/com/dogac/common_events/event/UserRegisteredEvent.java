package com.dogac.common_events.event;

import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId,
        String externalId,
        String email) {
}