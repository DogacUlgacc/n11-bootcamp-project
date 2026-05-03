package com.dogac.user_service.application.queryHandlers;

import org.springframework.stereotype.Component;

import com.dogac.user_service.application.core.QueryHandler;
import com.dogac.user_service.application.dto.UserIdentityResponse;
import com.dogac.user_service.application.queries.GetUserByExternalIdQuery;
import com.dogac.user_service.domain.entities.User;
import com.dogac.user_service.domain.exceptions.UserNotFoundExcepiton;
import com.dogac.user_service.domain.repositories.UserRepository;
import com.dogac.user_service.domain.valueobjects.ExternalId;

@Component
public class GetUserByExternalIdQueryHandler implements QueryHandler<GetUserByExternalIdQuery, UserIdentityResponse> {

    private final UserRepository userRepository;

    public GetUserByExternalIdQueryHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserIdentityResponse handle(GetUserByExternalIdQuery query) {
        User user = userRepository.findByExternalId(new ExternalId(query.externalId()))
                .orElseThrow(() -> new UserNotFoundExcepiton("User with given external id is not found"));

        return new UserIdentityResponse(user.getId().value());
    }
}
