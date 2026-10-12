package com.donajosy.api.auth.controller;

import com.donajosy.api.auth.dto.LoginRequest;
import com.donajosy.api.auth.dto.LoginResponse;
import com.donajosy.api.auth.dto.RegisterRequest;
import com.donajosy.api.auth.dto.RegisterResponse;
import com.donajosy.api.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<RegisterResponse> register(
            @RequestBody RegisterRequest request
    ) {

        RegisterResponse response =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<RegisterResponse> registerAdmin(
            @RequestBody RegisterRequest request
    ) {

        RegisterResponse response =
                authService.registerAdmin(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
