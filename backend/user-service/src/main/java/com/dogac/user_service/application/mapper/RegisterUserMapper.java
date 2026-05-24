package com.dogac.user_service.application.mapper;

import org.springframework.stereotype.Component;

import com.dogac.user_service.application.commands.RegisterUserCommand;
import com.dogac.user_service.application.dto.RegisteredUserResponse;
import com.dogac.user_service.domain.entities.User;
import com.dogac.user_service.domain.valueobjects.Email;
import com.dogac.user_service.domain.valueobjects.ExternalId;
import com.dogac.user_service.domain.valueobjects.FullName;
import com.dogac.user_service.domain.valueobjects.PhoneNumber;

@Component
public class RegisterUserMapper {

    public User toEntity(RegisterUserCommand command, ExternalId externalId) {

        return User.register(
                externalId,
                new FullName(command.firstName(), command.lastName()),
                new Email(command.email()),
                new PhoneNumber(command.phoneNumber()),
                command.userType());

    }

    public RegisteredUserResponse toResponse(User user) {

        return new RegisteredUserResponse(
                user.getId().value(),
                user.getExternalId().value(),

                user.getFullName().firstName(),
                user.getFullName().lastName(),

                user.getEmail().value(),
                user.getPhoneNumber().value(),

                user.getUserType(),
                user.getStatus());
    }
}
