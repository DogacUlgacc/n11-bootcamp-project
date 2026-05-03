package com.dogac.cart_service.application.commandHandlers;

import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.dogac.cart_service.application.commands.RemoveCartItemCommand;
import com.dogac.cart_service.domain.cart.Cart;
import com.dogac.cart_service.domain.cart.CartId;
import com.dogac.cart_service.domain.enums.Currency;
import com.dogac.cart_service.domain.repositories.CartRepository;
import com.dogac.cart_service.domain.valueobjects.Money;
import com.dogac.cart_service.domain.valueobjects.ProductId;
import com.dogac.cart_service.domain.valueobjects.Quantity;
import com.dogac.cart_service.domain.valueobjects.UserId;
import com.dogac.cart_service.infrastructure.kafka.KafkaEventPublisher;
import com.dogac.common_events.event.CartItemRemovedEvent;

@ExtendWith(MockitoExtension.class)
class RemoveCartItemCommandHandlerTest {

    @Mock
    private CartRepository cartRepository;

    @Test
    void shouldRemoveItemFromCart() {
        TestKafkaEventPublisher kafkaEventPublisher = new TestKafkaEventPublisher();
        RemoveCartItemCommandHandler handler = new RemoveCartItemCommandHandler(cartRepository, kafkaEventPublisher);
        UUID productId = UUID.randomUUID();
        Cart cart = Cart.create(UserId.from(UUID.randomUUID()), Currency.TRY);
        cart.addItem(ProductId.from(productId), Quantity.of(4), Money.from(BigDecimal.valueOf(10), Currency.TRY));
        RemoveCartItemCommand command = new RemoveCartItemCommand(cart.getId().value(), productId);
        when(cartRepository.findById(CartId.from(command.cartId()))).thenReturn(Optional.of(cart));

        handler.handle(command);

        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(cartCaptor.capture());
        assertTrue(cartCaptor.getValue().isEmpty());

        assertTrue(kafkaEventPublisher.cartItemRemovedEvent.cartId().equals(command.cartId()));
        assertTrue(kafkaEventPublisher.cartItemRemovedEvent.productId().equals(productId));
        assertTrue(kafkaEventPublisher.cartItemRemovedEvent.quantity().equals(4));
    }

    private static class TestKafkaEventPublisher extends KafkaEventPublisher {
        private CartItemRemovedEvent cartItemRemovedEvent;

        TestKafkaEventPublisher() {
            super((KafkaTemplate<String, Object>) null);
        }

        @Override
        public void publishRemoveCartItem(CartItemRemovedEvent event) {
            this.cartItemRemovedEvent = event;
        }
    }
}
