package com.donajosy.api.auth.dto;

import com.donajosy.api.product.entity.ProductCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record CreateUserRequest(
        @JsonProperty("name")
        @NotBlank String name,
        @JsonProperty("email")
        @NotBlank String email,
        @JsonProperty("phone")
        @NotNull ProductCategory phone,
        @JsonProperty("password")
        @NotNull MultipartFile password,
        @JsonProperty("isActive")
        Boolean isActive
) {}
