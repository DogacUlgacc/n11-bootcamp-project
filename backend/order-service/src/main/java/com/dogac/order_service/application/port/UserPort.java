package com.dogac.order_service.application.port;

import java.util.UUID;

import com.dogac.order_service.application.feignDto.UserDto;

public interface UserPort {
    UserDto getUserByExternalId(String externalId);

    UserDto getUserById(UUID id);
}
