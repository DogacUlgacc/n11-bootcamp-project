package com.dogac.product_service.application.dto;

import java.util.List;

public record PaginatedProductResponse(
        List<ProductResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}