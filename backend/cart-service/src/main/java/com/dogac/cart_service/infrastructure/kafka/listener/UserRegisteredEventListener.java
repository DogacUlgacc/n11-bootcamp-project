package com.dogac.cart_service.infrastructure.kafka.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.dogac.cart_service.domain.cart.Cart;
import com.dogac.cart_service.domain.enums.Currency;
import com.dogac.cart_service.domain.repositories.CartRepository;
import com.dogac.cart_service.domain.valueobjects.UserId;
import com.dogac.common_events.event.UserRegisteredEvent;

@Component
public class UserRegisteredEventListener {
    private final CartRepository cartRepository;

    public UserRegisteredEventListener(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @KafkaListener(topics = "user-registered", groupId = "cart-service")
    public void handleUserRegistered(UserRegisteredEvent event) {
        Cart cart = Cart.create(UserId.from(event.userId()), Currency.TRY);
        cartRepository.save(cart);
    }
}
