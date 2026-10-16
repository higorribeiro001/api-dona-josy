package com.donajosy.api.cart.controller;

import com.donajosy.api.cart.entity.Cart;
import com.donajosy.api.cart.dto.CreateCartRequest;
import com.donajosy.api.cart.entity.StatusCart;
import com.donajosy.api.cart.service.CartService;
import com.donajosy.api.product.entity.ProductCategory;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

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
            @RequestParam(required = false) StatusCart statusCart,
            @RequestParam(required = false) ProductCategory category,
            Pageable pageable,
            Authentication authentication
    ) {
        return cartService.search(name, category, statusCart, pageable, authentication);
    }

    @GetMapping("/total")
    public BigDecimal total(@RequestParam(required = false) StatusCart statusCart,
                            Authentication authentication) {
        return cartService.totalForUser(statusCart, authentication);
    }

    @GetMapping("/{id}")
    public Cart findById(@PathVariable UUID id, Authentication authentication) {
        return cartService.getCartById(id, authentication);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cart save(@Valid @RequestBody CreateCartRequest request, Authentication authentication) {
        return cartService.create(request.productId(), StatusCart.ANDAMENTO, authentication);
    }

    @PutMapping("/{id}")
    public Cart update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateCartRequest request,
            Authentication authentication
    ) throws IOException {
        return cartService.update(id, request.statusCart(), authentication);
    }
}


