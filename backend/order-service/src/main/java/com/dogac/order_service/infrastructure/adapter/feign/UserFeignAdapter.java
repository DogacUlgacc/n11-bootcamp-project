package com.dogac.order_service.infrastructure.adapter.feign;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dogac.order_service.application.feignDto.UserDto;
import com.dogac.order_service.application.port.UserPort;
import com.dogac.order_service.infrastructure.resilience.user.UserRetryService;

@Component
public class UserFeignAdapter implements UserPort {

    private final UserRetryService userRetryService;

    public UserFeignAdapter(UserRetryService userRetryService) {
        this.userRetryService = userRetryService;
    }

    @Override
    public UserDto getUserByExternalId(String externalId) {
        return userRetryService.getUserByExternalId(externalId);
    }

    @Override
    public UserDto getUserById(UUID id) {
        return userRetryService.getUserById(id);
    }
}