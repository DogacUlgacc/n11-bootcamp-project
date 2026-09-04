package com.dogac.cart_service.infrastructure.resilience;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.dogac.cart_service.application.dto.feignDto.Currency;
import com.dogac.cart_service.application.dto.feignDto.ProductDto;
import com.dogac.cart_service.infrastructure.feignclient.ProductClient;
import com.dogac.cart_service.infrastructure.resilience.product.ProductCircuitBreakerService;
import com.dogac.cart_service.infrastructure.resilience.product.ProductRetryService;

import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@SpringBootTest(classes = ProductResilienceIntegrationTest.TestConfig.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
class ProductResilienceIntegrationTest {

    @Configuration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            RedisAutoConfiguration.class,
            EurekaClientAutoConfiguration.class
    })
    @Import({ ProductCircuitBreakerService.class, ProductRetryService.class })
    static class TestConfig {
    }

    @MockBean
    private ProductClient productClient;

    @Autowired
    private ProductRetryService productRetryService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private final UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @BeforeEach
    void resetCircuitBreaker() {
        circuitBreakerRegistry.circuitBreaker("productService").reset();
    }

    @Test
    void shouldRetryUpToMaxAttemptsOnServiceUnavailable() {
        when(productClient.getProductById(productId))
                .thenThrow(serviceUnavailableException());

        assertThrows(FeignException.ServiceUnavailable.class,
                () -> productRetryService.getProductById(productId));

        verify(productClient, times(3)).getProductById(productId);
    }

    @Test
    void shouldRetryUpToMaxAttemptsOnRetryableException() {
        when(productClient.getProductById(productId))
                .thenThrow(retryableException("service unavailable"));

        assertThrows(RetryableException.class, () -> productRetryService.getProductById(productId));

        verify(productClient, times(3)).getProductById(productId);
    }

    @Test
    void shouldNotRetryWhenProductNotFound() {
        when(productClient.getProductById(productId))
                .thenThrow(notFoundException());

        assertThrows(FeignException.NotFound.class, () -> productRetryService.getProductById(productId));

        verify(productClient, times(1)).getProductById(productId);
    }

    @Test
    void shouldOpenCircuitBreakerAndUseFallbackAfterRepeatedFailures() {
        when(productClient.getProductById(any(UUID.class)))
                .thenThrow(new RuntimeException("product service down"));

        for (int i = 0; i < 5; i++) {
            assertThrows(RuntimeException.class, () -> productRetryService.getProductById(productId));
        }

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("productService");
        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());

        RuntimeException fallbackException = assertThrows(
                RuntimeException.class,
                () -> productRetryService.getProductById(productId));
        assertTrue(fallbackException.getMessage().contains("Product service is currently unavailable"));

        verify(productClient, times(5)).getProductById(productId);
    }

    @Test
    void shouldReturnProductWhenServiceIsAvailable() {
        ProductDto product = new ProductDto(
                productId,
                "Test Product",
                "Description",
                BigDecimal.TEN,
                Currency.TRY,
                5);

        when(productClient.getProductById(productId)).thenReturn(product);

        ProductDto result = productRetryService.getProductById(productId);

        assertEquals(product, result);
        verify(productClient, times(1)).getProductById(productId);
    }

    private FeignException.NotFound notFoundException() {
        Request request = Request.create(
                HttpMethod.GET,
                "/api/v1/products/" + productId,
                Collections.emptyMap(),
                null,
                null,
                null);

        return new FeignException.NotFound("not found", request, null, null);
    }

    private RetryableException retryableException(String message) {
        Request request = Request.create(
                HttpMethod.GET,
                "/api/v1/products/" + productId,
                Collections.emptyMap(),
                null,
                null,
                null);

        return new RetryableException(
                503,
                message,
                HttpMethod.GET,
                null,
                0L,
                request);
    }

    private FeignException.ServiceUnavailable serviceUnavailableException() {
        Request request = Request.create(
                HttpMethod.GET,
                "/api/v1/products/" + productId,
                Collections.emptyMap(),
                null,
                null,
                null);

        return new FeignException.ServiceUnavailable(
                "service unavailable",
                request,
                null,
                null);
    }
}
