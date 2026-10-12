package com.donajosy.api.product.dto;

import java.util.UUID;

public record ProductResponse(
        UUID id,
        String email,
        String name,
        String phone
) {}
