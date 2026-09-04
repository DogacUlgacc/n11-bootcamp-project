package com.dogac.cart_service.application.port;

import com.dogac.cart_service.application.dto.feignDto.UserDto;

public interface UserPort {
    public UserDto getUserByExternalId(String id);
}
