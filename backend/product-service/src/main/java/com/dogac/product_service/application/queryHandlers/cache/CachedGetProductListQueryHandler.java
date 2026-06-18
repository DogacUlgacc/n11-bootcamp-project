package com.dogac.product_service.application.queryHandlers.cache;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.dogac.product_service.application.core.QueryHandler;
import com.dogac.product_service.application.dto.ProductResponse;
import com.dogac.product_service.application.queries.GetProductListQuery;
import com.dogac.product_service.application.queryHandlers.GetProductListQueryHandler;

@Component
public class CachedGetProductListQueryHandler implements QueryHandler<GetProductListQuery, List<ProductResponse>> {
    private final GetProductListQueryHandler delegate;

    public CachedGetProductListQueryHandler(GetProductListQueryHandler delegate) {
        this.delegate = delegate;
    }

    @Cacheable(value = "product-list", key = "'all-products'")
    public List<ProductResponse> handle(GetProductListQuery query) {
        return delegate.handle(query);
    }

}
