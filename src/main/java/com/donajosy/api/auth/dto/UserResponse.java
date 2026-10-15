package com.donajosy.api.auth.dto;

import com.donajosy.api.auth.entity.Role;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String name,
        String phone,
        Role role,
        String address,
        String num,
        String neighborhood
) {}
