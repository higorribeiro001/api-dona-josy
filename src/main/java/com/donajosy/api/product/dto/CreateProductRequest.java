package com.donajosy.api.product.dto;

import com.donajosy.api.product.entity.ProductCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record CreateProductRequest(
        @JsonProperty("name")
        @NotBlank String name,
        @JsonProperty("description")
        @NotBlank String description,
        @JsonProperty("category")
        @NotNull ProductCategory category,
        @JsonProperty("image")
        @NotNull MultipartFile image,
        @JsonProperty("price")
        @NotNull BigDecimal price,
        @JsonProperty("isActive")
        Boolean isActive
) {}
