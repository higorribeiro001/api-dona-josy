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

    public Page<Cart> getCarts(Pageable pageable) { return cartRepository.findAll(pageable); }

    public Page<Cart> getCartsByName(String name, Pageable pageable) { return cartRepository.findByProduct_NameContainingIgnoreCase(name, pageable); }

    public Cart getCartById(UUID id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
    }

    public Cart create(UUID userId, UUID productId, StatusCart statusCart) {
        User user = userRepository.findById(userId)
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

    public Cart update(UUID id, Cart cart) {
        if (cartRepository.existsById(id)) {
            cart.setId(id);
            return cartRepository.save(cart);
        } else {
            throw new RuntimeException("Cart with id: " + id + " not found");
        }
    }
}


