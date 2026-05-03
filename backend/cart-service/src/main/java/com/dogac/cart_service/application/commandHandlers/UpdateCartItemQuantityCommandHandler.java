package com.dogac.cart_service.application.commandHandlers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dogac.cart_service.application.commands.UpdateCartItemQuantityCommand;
import com.dogac.cart_service.application.core.CommandHandler;
import com.dogac.cart_service.application.dto.CartResponse;
import com.dogac.cart_service.application.mapper.CartResponseMapper;
import com.dogac.cart_service.domain.cart.Cart;
import com.dogac.cart_service.domain.cart.CartId;
import com.dogac.cart_service.domain.cart.CartItem;
import com.dogac.cart_service.domain.exceptions.CartNotFoundException;
import com.dogac.cart_service.domain.repositories.CartRepository;
import com.dogac.cart_service.domain.valueobjects.ProductId;
import com.dogac.cart_service.domain.valueobjects.Quantity;
import com.dogac.cart_service.infrastructure.kafka.KafkaEventPublisher;
import com.dogac.cart_service.web.CartController;
import com.dogac.common_events.event.CartItemQuantityUpdatedEvent;

@Component
public class UpdateCartItemQuantityCommandHandler
        implements CommandHandler<UpdateCartItemQuantityCommand, CartResponse> {

    private final CartRepository cartRepository;
    private final CartResponseMapper cartResponseMapper;
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private final KafkaEventPublisher kafkaEventPublisher;

    public UpdateCartItemQuantityCommandHandler(CartRepository cartRepository, CartResponseMapper cartResponseMapper,
            KafkaEventPublisher kafkaEventPublisher) {
        this.cartRepository = cartRepository;
        this.cartResponseMapper = cartResponseMapper;
        this.kafkaEventPublisher = kafkaEventPublisher;
    }

    // *TODO:: Şuan keycloack olmadığı için findByIdAndUserId() yerine findById
    // kullanıyoruz!*/

    @Override
    public CartResponse handle(UpdateCartItemQuantityCommand command) {
        Cart cart = cartRepository.findById(
                CartId.from(command.cartId()))
                .orElseThrow(() -> new CartNotFoundException("cart not found!"));
        logger.info("buraya bak" + cart.getId());

        List<CartItem> list = cart.getItems();
        for (CartItem cartItem : list) {
            System.out.println(cartItem);
        }
        CartItem item = cart.getItems()
                .stream()
                .filter(i -> i.getProductId().equals(ProductId.from(command.productId())))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cart item not found"));

        int oldQuantity = item.getQuantity().value();
        int newQuantity = command.quantity();

        int delta = newQuantity - oldQuantity;

        cart.changeItemQuantity(ProductId.from(command.productId()), new Quantity(command.quantity()));

        cartRepository.save(cart);

        CartItemQuantityUpdatedEvent event = new CartItemQuantityUpdatedEvent(
                cart.getId().value(),
                item.getProductId().value(),
                oldQuantity,
                newQuantity,
                delta);
        kafkaEventPublisher.publishCartItemUpdated(event);
        logger.info("event gitti mi ? " + event.toString());
        return cartResponseMapper.toResponse(cart);
    }

}
