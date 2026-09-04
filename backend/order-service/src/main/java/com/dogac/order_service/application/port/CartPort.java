package com.dogac.order_service.application.port;

import java.util.UUID;

import com.dogac.order_service.application.feignDto.CartDto;

public interface CartPort {

    CartDto getCartById(UUID id);

}