package com.donajosy.api.cart.service;

import com.donajosy.api.cart.entity.Cart;
import com.donajosy.api.cart.repository.CartRepository;
import com.donajosy.api.auth.entity.User;
import com.donajosy.api.auth.repository.UserRepository;
import com.donajosy.api.product.entity.Product;
import com.donajosy.api.product.repository.ProductRepository;
import com.donajosy.api.cart.entity.StatusCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    public Page<Cart> getCarts(Pageable pageable, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        if (isAdmin) {
            return cartRepository.findAll(pageable);
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        return cartRepository.findByUser_Id(user.getId(), pageable);
    }

    public Page<Cart> getCartsByName(String name, Pageable pageable) { return cartRepository.findByProduct_NameContainingIgnoreCase(name, pageable); }

    public Page<Cart> getCartsByStatusCart(StatusCart statusCart, Pageable pageable, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        if (isAdmin) {
            return cartRepository.findAllByStatusCart(statusCart, pageable);
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        return cartRepository.findAllByUser_IdAndStatusCart(user.getId(), statusCart, pageable);
    }

    public Page<Cart> getCartsByNameAndStatusCart(String name, StatusCart statusCart, Pageable pageable) {
        return cartRepository.findAllByProduct_NameContainingIgnoreCaseAndStatusCart(name, statusCart, pageable);
    }

    public Cart getCartById(UUID id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
    }

    public Cart create(UUID productId, StatusCart statusCart, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        Cart cart = new Cart(user, product, statusCart);
        return cartRepository.save(cart);
    }

    public Cart save(Cart cart) { return cartRepository.save(cart); }

    public void deleteById(UUID id) {
        cartRepository.deleteById(id);
    }

    public Cart update(UUID id, StatusCart statusCart) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Cart with id: " + id + " not found"
                        )
                );

        cart.setStatusCart(statusCart);

        return cartRepository.save(cart);
    }
}


