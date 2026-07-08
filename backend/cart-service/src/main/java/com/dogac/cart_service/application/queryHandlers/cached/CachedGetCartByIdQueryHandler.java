package com.dogac.cart_service.application.queryHandlers.cached;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.dogac.cart_service.application.core.QueryHandler;
import com.dogac.cart_service.application.dto.CartResponse;
import com.dogac.cart_service.application.queries.GetCartByIdQuery;
import com.dogac.cart_service.application.queryHandlers.GetCartByIdQueryHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CachedGetCartByIdQueryHandler implements QueryHandler<GetCartByIdQuery, CartResponse> {

    private final GetCartByIdQueryHandler delegate;

    public CachedGetCartByIdQueryHandler(GetCartByIdQueryHandler delegate) {
        this.delegate = delegate;
    }

    @Cacheable(value = "cart-by-id", key = "#query.cartId()")
    @Override
    public CartResponse handle(GetCartByIdQuery query) {
        log.info("CACHE METHOD CALLED GetCartByIdQueryHandler");
        return delegate.handle(query);
    }
}
