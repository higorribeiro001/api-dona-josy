package com.donajosy.api.cart.dto;

import com.donajosy.api.cart.entity.StatusCart;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateCartRequest(
        @JsonProperty("product_id")
        @NotNull UUID productId,
        @JsonProperty("status_cart")
        StatusCart statusCart
) {}
