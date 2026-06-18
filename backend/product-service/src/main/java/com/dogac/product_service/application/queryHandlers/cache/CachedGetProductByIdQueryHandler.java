package com.dogac.product_service.application.queryHandlers.cache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.dogac.product_service.application.core.QueryHandler;
import com.dogac.product_service.application.dto.ProductResponse;
import com.dogac.product_service.application.queries.GetProductByIdQuery;
import com.dogac.product_service.application.queryHandlers.GetProductByIdQueryHandler;

@Component
public class CachedGetProductByIdQueryHandler implements QueryHandler<GetProductByIdQuery, ProductResponse> {

    private final GetProductByIdQueryHandler delegate;

    public CachedGetProductByIdQueryHandler(GetProductByIdQueryHandler delegate) {
        this.delegate = delegate;
    }

    @Cacheable(value = "products", key = "#query.id()")
    @Override
    public ProductResponse handle(GetProductByIdQuery query) {

        return delegate.handle(query);
    }
}
