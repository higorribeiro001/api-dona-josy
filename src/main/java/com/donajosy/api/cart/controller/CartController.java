package com.donajosy.api.cart.controller;

import com.donajosy.api.auth.entity.User;
import com.donajosy.api.cart.entity.Cart;
import com.donajosy.api.cart.dto.CreateCartRequest;
import com.donajosy.api.cart.entity.StatusCart;
import com.donajosy.api.cart.service.CartService;
import com.donajosy.api.product.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/carts")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping
    public Page<Cart> findAll(
            @RequestParam(required = false) String name,
            Pageable pageable
    ) {
        if (name != null) {
            return cartService.getCartsByName(name, pageable);
        }

        return cartService.getCarts(pageable);
    }

    @GetMapping("/{id}")
    public Cart findById(@PathVariable UUID id) {
        return cartService.getCartById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cart save(@Valid @RequestBody CreateCartRequest request) {
        return cartService.create(request.userId(), request.productId(), StatusCart.ANDAMENTO);
    }

    @PutMapping("/{id}")
    public Cart update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateCartRequest request
    ) throws IOException {
        Cart cart = cartService.getCartById(id);

        if  (cart == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
        }

        return cartService.create(request.userId(), request.productId(), request.statusCart());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        cartService.deleteById(id);
    }
}


