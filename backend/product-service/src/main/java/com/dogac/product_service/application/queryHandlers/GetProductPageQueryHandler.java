package com.dogac.product_service.application.queryHandlers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.dogac.product_service.application.core.QueryHandler;
import com.dogac.product_service.application.dto.PaginatedProductResponse;
import com.dogac.product_service.application.dto.ProductResponse;
import com.dogac.product_service.application.queries.GetProductPageQuery;
import com.dogac.product_service.domain.entities.Product;
import com.dogac.product_service.domain.repositories.ProductRepository;

@Component
public class GetProductPageQueryHandler implements QueryHandler<GetProductPageQuery, PaginatedProductResponse> {

    private final ProductRepository productRepository;

    public GetProductPageQueryHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public PaginatedProductResponse handle(GetProductPageQuery query) {

        Sort sort = Sort.by(
                query.direction().equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                query.sortBy());

        Pageable pageable = PageRequest.of(
                query.page(),
                query.size(),
                sort);

        Page<Product> pageResult = productRepository.findAll(pageable);

        List<ProductResponse> content = pageResult.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return new PaginatedProductResponse(
                content,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages());
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(product.getId().value(), product.getName().value(), product.getDescription().value(),
                product.getPrice().amount(), product.getPrice().currency(), product.getStockQuantity().value());
    }
}
