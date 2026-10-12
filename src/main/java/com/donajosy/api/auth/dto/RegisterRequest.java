package com.donajosy.api.auth.dto;

public record RegisterRequest(
        String name,
        String email,
        String phone,
        String password
) {}
