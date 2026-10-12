package com.donajosy.api.auth.dto;

public record LoginRequest(
        String email,
        String password
) {}
