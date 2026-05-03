package com.dogac.product_service.application.queries;

import com.dogac.product_service.application.core.Query;
import com.dogac.product_service.application.dto.PaginatedProductResponse;

public record GetProductPageQuery(
        int page,
        int size,
        String sortBy,
        String direction) implements Query<PaginatedProductResponse> {
}