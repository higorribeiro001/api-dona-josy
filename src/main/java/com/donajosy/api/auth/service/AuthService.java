package com.donajosy.api.auth.service;

import com.donajosy.api.auth.dto.RegisterRequest;
import com.donajosy.api.auth.dto.RegisterResponse;
import com.donajosy.api.auth.entity.Role;
import com.donajosy.api.auth.entity.User;
import com.donajosy.api.auth.repository.UserRepository;
import com.donajosy.api.auth.security.JwtService;
import com.donajosy.api.token.entity.RevokedToken;
import com.donajosy.api.token.repository.RevokedTokenRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RevokedTokenRepository revokedTokenRepository;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder, RevokedTokenRepository revokedTokenRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.revokedTokenRepository = revokedTokenRepository;
    }

    public String login(String email, String password) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .roles(user.getRole().name())
                        .build();

        return jwtService.generateToken(userDetails);
    }

    public void logout(String token) {

        LocalDateTime expiresAt =
                jwtService.extractExpiration(token)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

        RevokedToken revokedToken =
                new RevokedToken(token, expiresAt);

        revokedTokenRepository.save(revokedToken);
    }

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();

        user.setName(request.name());

        user.setEmail(request.email());

        user.setPhone(request.phone());

        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        // Todo usuário novo começa como USER
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getPhone(),
                savedUser.getRole()
        );
    }

    public RegisterResponse registerAdmin(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();

        user.setName(request.name());

        user.setEmail(request.email());

        user.setPhone(request.phone());

        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        user.setRole(Role.ADMIN);

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getPhone(),
                savedUser.getRole()
        );
    }
}
