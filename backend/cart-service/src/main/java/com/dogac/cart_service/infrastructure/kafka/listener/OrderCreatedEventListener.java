package com.dogac.cart_service.infrastructure.kafka.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dogac.cart_service.domain.cart.Cart;
import com.dogac.cart_service.domain.cart.CartId;
import com.dogac.cart_service.domain.exceptions.CartNotFoundException;
import com.dogac.cart_service.domain.repositories.CartRepository;
import com.dogac.common_events.event.OrderCreatedEvent;

@Component
public class OrderCreatedEventListener {

    private final CartRepository cartRepository;

    public OrderCreatedEventListener(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @KafkaListener(topics = "order-created", groupId = "cart-service")
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {

        Cart cart = cartRepository.findById(CartId.from(event.cartId()))
                .orElseThrow(() -> new CartNotFoundException("CartNotFound!"));
        cart.clear();
        cartRepository.save(cart);

    }
}
