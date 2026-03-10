package com.sa.event_mng.service;

import com.sa.event_mng.dto.request.CartItemRequest;
import com.sa.event_mng.dto.response.CartResponse;
import com.sa.event_mng.exception.AppException;
import com.sa.event_mng.exception.ErrorCode;
import com.sa.event_mng.mapper.CartMapper;
import com.sa.event_mng.model.entity.*;
import com.sa.event_mng.model.enums.CartStatus;
import com.sa.event_mng.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {

    CartRepository cartRepository;
    UserRepository userRepository;
    TicketTypeRepository ticketTypeRepository;
    CartMapper cartMapper;

    @Transactional
    public CartResponse addToCart(CartItemRequest request) {
        User user = getCurrentUser();
        Cart cart = cartRepository.findByCustomerId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder()
                        .customer(user)
                        .status(CartStatus.ACTIVE)
                        .items(new ArrayList<>())
                        .build()));

        TicketType ticketType = ticketTypeRepository.findById(request.getTicketTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        // Check if item already in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getTicketType().getId().equals(request.getTicketTypeId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            item.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .ticketType(ticketType)
                    .quantity(request.getQuantity())
                    .unitPrice(ticketType.getPrice())
                    .subtotal(ticketType.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())))
                    .build();
            cart.getItems().add(newItem);
        }

        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    public CartResponse getMyCart() {
        User user = getCurrentUser();
        Cart cart = cartRepository.findByCustomerId(user.getId())
                .orElseGet(() -> Cart.builder().items(new ArrayList<>()).build());
        return cartMapper.toCartResponse(cart);
    }

    @Transactional
    public void clearCart() {
        User user = getCurrentUser();
        cartRepository.findByCustomerId(user.getId()).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

}
