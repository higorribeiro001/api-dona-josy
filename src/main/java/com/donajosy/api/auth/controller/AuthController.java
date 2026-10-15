package com.donajosy.api.auth.controller;

import com.donajosy.api.auth.dto.LoginRequest;
import com.donajosy.api.auth.dto.LoginResponse;
import com.donajosy.api.auth.dto.UserRequest;
import com.donajosy.api.auth.dto.UserResponse;
import com.donajosy.api.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {

        String token = authService.login(
                request.email(),
                request.password()
        );

        return ResponseEntity.ok(
                new LoginResponse(token)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authorization
    ) {

        String token = authorization.substring(7);

        authService.logout(token);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestBody UserRequest request
    ) {

        UserResponse response =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<UserResponse> registerAdmin(
            @RequestBody UserRequest request
    ) {

        UserResponse response =
                authService.registerAdmin(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id, @RequestBody UserRequest request
    ) {

        UserResponse response =
                authService.update(id, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable UUID id
    ) {

        UserResponse response =
                authService.profile(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> profile(Authentication authentication) {
        return ResponseEntity.ok(authService.profile(authentication.getName()));
    }
}
