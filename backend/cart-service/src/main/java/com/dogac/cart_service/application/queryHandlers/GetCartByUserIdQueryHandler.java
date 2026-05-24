package com.dogac.cart_service.application.queryHandlers;

import org.springframework.stereotype.Component;

import com.dogac.cart_service.application.core.QueryHandler;
import com.dogac.cart_service.application.dto.CartResponse;
import com.dogac.cart_service.application.mapper.CartResponseMapper;
import com.dogac.cart_service.application.queries.GetCartByUserIdQuery;
import com.dogac.cart_service.domain.cart.Cart;
import com.dogac.cart_service.domain.exceptions.CartNotFoundException;
import com.dogac.cart_service.domain.repositories.CartRepository;
import com.dogac.cart_service.domain.valueobjects.UserId;

@Component
public class GetCartByUserIdQueryHandler implements QueryHandler<GetCartByUserIdQuery, CartResponse> {

    private final CartRepository cartRepository;
    private final CartResponseMapper cartResponseMapper;

    public GetCartByUserIdQueryHandler(CartRepository cartRepository, CartResponseMapper cartResponseMapper) {
        this.cartRepository = cartRepository;
        this.cartResponseMapper = cartResponseMapper;
    }

    @Override
    public CartResponse handle(GetCartByUserIdQuery query) {
        Cart optionalCart = cartRepository.findByUserId(UserId.from(query.userId()))
                .orElseThrow(() -> new CartNotFoundException("CartNotFound!"));

        return cartResponseMapper.toResponse(optionalCart);
    }

}
