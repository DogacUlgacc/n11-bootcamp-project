package com.dogac.user_service.application.commandHandlers;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dogac.common_events.event.UserRegisteredEvent;
import com.dogac.user_service.application.commands.RegisterUserCommand;
import com.dogac.user_service.application.core.CommandHandler;
import com.dogac.user_service.application.dto.RegisteredUserResponse;
import com.dogac.user_service.application.mapper.RegisterUserMapper;
import com.dogac.user_service.domain.entities.User;
import com.dogac.user_service.domain.repositories.UserRepository;
import com.dogac.user_service.domain.services.UserDomainService;
import com.dogac.user_service.domain.valueobjects.Email;
import com.dogac.user_service.domain.valueobjects.ExternalId;
import com.dogac.user_service.domain.valueobjects.PhoneNumber;
import com.dogac.user_service.infrastructure.kafka.KafkaEventPublisher;
import com.dogac.user_service.infrastructure.keycloack.KeycloakUserProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Transactional
@Slf4j
public class RegisterUserCommandHandler implements CommandHandler<RegisterUserCommand, RegisteredUserResponse> {

    private final UserRepository userRepository;
    private final RegisterUserMapper registerUserMapper;
    private final KeycloakUserProvider keycloakUserProvider;
    private final UserDomainService userDomainService;
    private final KafkaEventPublisher kafkaEventPublisher;

    public RegisterUserCommandHandler(UserRepository userRepository, RegisterUserMapper registerUserMapper,
            KeycloakUserProvider keycloakUserProvider, UserDomainService userDomainService,
            KafkaEventPublisher kafkaEventPublisher) {
        this.userRepository = userRepository;
        this.registerUserMapper = registerUserMapper;
        this.keycloakUserProvider = keycloakUserProvider;
        this.userDomainService = userDomainService;
        this.kafkaEventPublisher = kafkaEventPublisher;
    }

    @Override
    @Transactional
    public RegisteredUserResponse handle(RegisterUserCommand command) {
        userDomainService.ensureEmailIsUnique(new Email(command.email()));
        userDomainService.ensurePhoneNumberIsUnique(new PhoneNumber(command.phoneNumber()));

        String keycloakUserId = keycloakUserProvider.createUser(
                command.email(),
                command.password(),
                command.firstName(),
                command.lastName());
        log.info("keycloakUserId " + keycloakUserId);
        User user = registerUserMapper.toEntity(
                command,
                new ExternalId(keycloakUserId));

        if (command.addresses() != null) {
            command.addresses().forEach(user::addAddress);
        }

        User savedUser = userRepository.save(user);

        UserRegisteredEvent userRegisteredEvent = new UserRegisteredEvent(
                savedUser.getId().value(),
                savedUser.getExternalId().value(),
                savedUser.getEmail().value());

        kafkaEventPublisher.publishUserRegistered(userRegisteredEvent);
        log.info("UserRegisteredEvent published!" + userRegisteredEvent);
        return registerUserMapper.toResponse(savedUser);

    }
}
