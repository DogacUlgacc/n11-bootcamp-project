package com.dogac.cart_service.application.queryHandlers.cached;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.dogac.cart_service.application.core.QueryHandler;
import com.dogac.cart_service.application.dto.CartResponse;
import com.dogac.cart_service.application.queries.GetCartByUserIdQuery;
import com.dogac.cart_service.application.queryHandlers.GetCartByUserIdQueryHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CachedGetCartByUserIdQueryHandler implements QueryHandler<GetCartByUserIdQuery, CartResponse> {
    private final GetCartByUserIdQueryHandler delegate;

    public CachedGetCartByUserIdQueryHandler(GetCartByUserIdQueryHandler delegate) {
        this.delegate = delegate;
    }

    @Cacheable(value = "cart-by-user", key = "#query.userId()")
    @Override
    public CartResponse handle(GetCartByUserIdQuery query) {
        log.info("CACHE METHOD CALLED CachedGetCartByUserIdQueryHandler");
        return delegate.handle(query);
    }
}
