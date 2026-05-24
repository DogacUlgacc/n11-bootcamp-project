package com.dogac.user_service.application.commands;

import java.util.List;

import com.dogac.user_service.application.core.Command;
import com.dogac.user_service.application.dto.RegisteredUserResponse;
import com.dogac.user_service.domain.enums.UserType;
import com.dogac.user_service.domain.valueobjects.Address;

public record RegisterUserCommand(
                String firstName,
                String lastName,
                String email,
                String phoneNumber,
                String password,
                UserType userType,
                List<Address> addresses) implements Command<RegisteredUserResponse> {

}
