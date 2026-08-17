package com.dogac.cart_service.infrastructure.resilience;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dogac.cart_service.application.dto.feignDto.ProductDto;
import com.dogac.cart_service.infrastructure.feignclient.ProductClient;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductRetryService {

    private final ProductClient productClient;

    public ProductRetryService(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Retry(name = "productService")
    public ProductDto getProductById(UUID id) {

        log.info("Calling Product Service: {}", id);

        return productClient.getProductById(id);
    }
}