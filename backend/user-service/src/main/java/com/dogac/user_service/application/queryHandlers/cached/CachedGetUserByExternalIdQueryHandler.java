package com.dogac.user_service.application.queryHandlers.cached;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.dogac.user_service.application.core.QueryHandler;
import com.dogac.user_service.application.dto.UserIdentityResponse;
import com.dogac.user_service.application.queries.GetUserByExternalIdQuery;
import com.dogac.user_service.application.queryHandlers.GetUserByExternalIdQueryHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CachedGetUserByExternalIdQueryHandler
        implements QueryHandler<GetUserByExternalIdQuery, UserIdentityResponse> {
    private final GetUserByExternalIdQueryHandler delegate;

    public CachedGetUserByExternalIdQueryHandler(GetUserByExternalIdQueryHandler delegate) {
        this.delegate = delegate;
    }

    @Cacheable(value = "users-by-external-id", key = "#query.externalId()")
    @Override
    public UserIdentityResponse handle(GetUserByExternalIdQuery query) {
        log.info("CACHE METHOD CALLED GetUserByExternalIdQueryHandler");
        return delegate.handle(query);
    }

}
