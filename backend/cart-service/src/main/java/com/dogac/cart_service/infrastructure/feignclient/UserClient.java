package com.dogac.cart_service.infrastructure.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dogac.cart_service.application.dto.feignDto.UserDto;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/v1/users/by-external-id/{externalId}")
    UserDto getUserByExternalId(@PathVariable String externalId);
}