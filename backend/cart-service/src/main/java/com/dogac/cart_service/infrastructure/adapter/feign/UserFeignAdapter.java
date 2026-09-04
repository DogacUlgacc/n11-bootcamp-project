package com.dogac.cart_service.infrastructure.adapter.feign;

import org.springframework.stereotype.Component;

import com.dogac.cart_service.application.dto.feignDto.UserDto;
import com.dogac.cart_service.application.port.UserPort;
import com.dogac.cart_service.infrastructure.resilience.user.UserRetryService;

@Component
public class UserFeignAdapter implements UserPort {
    private final UserRetryService userRetryService;

    public UserFeignAdapter(UserRetryService userRetryService) {
        this.userRetryService = userRetryService;
    }

    @Override
    public UserDto getUserByExternalId(String id) {
        return userRetryService.getUserByExternalId(id);
    }
}
