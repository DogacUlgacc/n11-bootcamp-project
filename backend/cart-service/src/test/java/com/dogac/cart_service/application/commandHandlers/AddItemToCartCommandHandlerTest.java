package com.dogac.cart_service.application.commandHandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.dogac.cart_service.application.commands.AddItemToCartCommand;
import com.dogac.cart_service.application.dto.feignDto.Currency;
import com.dogac.cart_service.application.dto.feignDto.ProductDto;
import com.dogac.cart_service.application.port.ProductPort;
import com.dogac.cart_service.domain.cart.Cart;
import com.dogac.cart_service.domain.repositories.CartRepository;
import com.dogac.cart_service.domain.valueobjects.UserId;
import com.dogac.cart_service.infrastructure.kafka.KafkaEventPublisher;
import com.dogac.common_events.event.CartItemAddedEvent;

@ExtendWith(MockitoExtension.class)
class AddItemToCartCommandHandlerTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductPort productPort;

    @Test
    void shouldAddItemToCart() {
        TestKafkaEventPublisher kafkaEventPublisher = new TestKafkaEventPublisher();
        AddItemToCartCommandHandler handler = new AddItemToCartCommandHandler(
                cartRepository,
                productPort,
                kafkaEventPublisher);
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Cart cart = Cart.create(UserId.from(userId), com.dogac.cart_service.domain.enums.Currency.TRY);
        AddItemToCartCommand command = new AddItemToCartCommand(userId, productId, 3);
        when(cartRepository.findByUserId(UserId.from(userId))).thenReturn(Optional.of(cart));
        when(productPort.getProductById(productId)).thenReturn(new ProductDto(
                productId,
                "Product",
                "Description",
                BigDecimal.valueOf(25),
                Currency.TRY,
                10));

        handler.handle(command);

        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(cartCaptor.capture());
        Cart savedCart = cartCaptor.getValue();
        assertEquals(1, savedCart.getItems().size());
        assertEquals(productId, savedCart.getItems().get(0).getProductId().value());
        assertEquals(3, savedCart.getItems().get(0).getQuantity().value());
        assertEquals(BigDecimal.valueOf(25).setScale(2), savedCart.getItems().get(0).getPrice().amount());

        assertEquals(savedCart.getId().value(), kafkaEventPublisher.cartItemAddedEvent.cartId());
        assertEquals(userId, kafkaEventPublisher.cartItemAddedEvent.userId());
        assertEquals(productId, kafkaEventPublisher.cartItemAddedEvent.productId());
        assertEquals(3, kafkaEventPublisher.cartItemAddedEvent.quantity());
        assertEquals(BigDecimal.valueOf(25).setScale(2), kafkaEventPublisher.cartItemAddedEvent.price());
        assertEquals(com.dogac.common_events.enums.Currency.TRY, kafkaEventPublisher.cartItemAddedEvent.currency());
    }

    private static class TestKafkaEventPublisher extends KafkaEventPublisher {
        private CartItemAddedEvent cartItemAddedEvent;

        TestKafkaEventPublisher() {
            super((KafkaTemplate<String, Object>) null);
        }

        @Override
        public void publishCartItemAdded(CartItemAddedEvent event) {
            this.cartItemAddedEvent = event;
        }
    }
}
