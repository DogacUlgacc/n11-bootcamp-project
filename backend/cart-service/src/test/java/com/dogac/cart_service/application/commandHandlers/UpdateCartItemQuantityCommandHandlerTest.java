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

import com.dogac.cart_service.application.commands.UpdateCartItemQuantityCommand;
import com.dogac.cart_service.application.dto.CartResponse;
import com.dogac.cart_service.application.mapper.CartResponseMapper;
import com.dogac.cart_service.domain.cart.Cart;
import com.dogac.cart_service.domain.cart.CartId;
import com.dogac.cart_service.domain.enums.Currency;
import com.dogac.cart_service.domain.repositories.CartRepository;
import com.dogac.cart_service.domain.valueobjects.Money;
import com.dogac.cart_service.domain.valueobjects.ProductId;
import com.dogac.cart_service.domain.valueobjects.Quantity;
import com.dogac.cart_service.domain.valueobjects.UserId;
import com.dogac.cart_service.infrastructure.kafka.KafkaEventPublisher;
import com.dogac.common_events.event.CartItemQuantityUpdatedEvent;

@ExtendWith(MockitoExtension.class)
class UpdateCartItemQuantityCommandHandlerTest {

    @Mock
    private CartRepository cartRepository;

    @Test
    void shouldUpdateItemQuantity() {
        TestKafkaEventPublisher kafkaEventPublisher = new TestKafkaEventPublisher();
        UpdateCartItemQuantityCommandHandler handler = new UpdateCartItemQuantityCommandHandler(
                cartRepository,
                new CartResponseMapper() {
                },
                kafkaEventPublisher);
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Cart cart = Cart.create(UserId.from(userId), Currency.TRY);
        cart.addItem(ProductId.from(productId), Quantity.of(2), Money.from(BigDecimal.valueOf(15), Currency.TRY));
        UpdateCartItemQuantityCommand command = new UpdateCartItemQuantityCommand(
                cart.getId().value(),
                userId,
                productId,
                5);
        when(cartRepository.findById(CartId.from(command.cartId()))).thenReturn(Optional.of(cart));

        CartResponse response = handler.handle(command);

        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(cartCaptor.capture());
        Cart savedCart = cartCaptor.getValue();
        assertEquals(5, savedCart.getItems().get(0).getQuantity().value());
        assertEquals(5, response.items().get(0).quantity());

        assertEquals(savedCart.getId().value(), kafkaEventPublisher.cartItemQuantityUpdatedEvent.cartId());
        assertEquals(productId, kafkaEventPublisher.cartItemQuantityUpdatedEvent.productId());
        assertEquals(2, kafkaEventPublisher.cartItemQuantityUpdatedEvent.oldQuantity());
        assertEquals(5, kafkaEventPublisher.cartItemQuantityUpdatedEvent.newQuantity());
        assertEquals(3, kafkaEventPublisher.cartItemQuantityUpdatedEvent.delta());
    }

    private static class TestKafkaEventPublisher extends KafkaEventPublisher {
        private CartItemQuantityUpdatedEvent cartItemQuantityUpdatedEvent;

        TestKafkaEventPublisher() {
            super((KafkaTemplate<String, Object>) null);
        }

        @Override
        public void publishCartItemUpdated(CartItemQuantityUpdatedEvent event) {
            this.cartItemQuantityUpdatedEvent = event;
        }
    }
}
