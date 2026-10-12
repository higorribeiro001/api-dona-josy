package com.donajosy.api.token.repository;

import com.donajosy.api.token.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RevokedTokenRepository
        extends JpaRepository<RevokedToken, UUID> {

    boolean existsByToken(String token);
}