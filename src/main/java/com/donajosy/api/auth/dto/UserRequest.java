package com.donajosy.api.auth.dto;
import java.util.UUID;

public record UserRequest(
        UUID id,
        String name,
        String email,
        String phone,
        String address,
        String num,
        String neighborhood,
        String password
) {}
