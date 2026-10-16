package com.donajosy.api.auth.service;

import com.donajosy.api.auth.dto.UserRequest;
import com.donajosy.api.auth.dto.UserResponse;
import com.donajosy.api.auth.entity.Role;
import com.donajosy.api.auth.entity.User;
import com.donajosy.api.auth.repository.UserRepository;
import com.donajosy.api.auth.security.JwtService;
import com.donajosy.api.token.entity.RevokedToken;
import com.donajosy.api.token.repository.RevokedTokenRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

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

    public UserResponse register(UserRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();

        user.setName(request.name());

        user.setEmail(request.email());

        user.setPhone(request.phone());

        user.setAddress(request.address());

        user.setNum(request.num());

        user.setNeighborhood(request.neighborhood());

        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        // Todo usuário novo começa como USER
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getPhone(),
                savedUser.getRole(),
                user.getAddress(),
                user.getNum(),
                user.getNeighborhood()
        );
    }

    public UserResponse update(UUID id, UserRequest request, Authentication authentication) {
        User user = userRepository.getReferenceById(id);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwnProfile = user.getEmail().equalsIgnoreCase(authentication.getName());
        if (!isAdmin && !isOwnProfile) {
            throw new AccessDeniedException("You can only update your own profile");
        }
        if (!user.getEmail().equalsIgnoreCase(request.email())
                && userRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        user.setName(request.name());

        user.setEmail(request.email());

        user.setPhone(request.phone());

        user.setAddress(request.address());

        user.setNum(request.num());

        user.setNeighborhood(request.neighborhood());

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getPhone(),
                savedUser.getRole(),
                user.getAddress(),
                user.getNum(),
                user.getNeighborhood()
        );
    }

    public UserResponse profile(UUID id) {
        User user = userRepository.getReferenceById(id);

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getRole(),
                user.getAddress(),
                user.getNum(),
                user.getNeighborhood()
        );
    }

    public UserResponse profile(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow();
        return new UserResponse(
                user.getId(), user.getEmail(), user.getName(), user.getPhone(),
                user.getRole(), user.getAddress(), user.getNum(), user.getNeighborhood()
        );
    }

    public UserResponse registerAdmin(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();

        user.setName(request.name());

        user.setEmail(request.email());

        user.setPhone(request.phone());

        user.setAddress(request.address());

        user.setNum(request.num());

        user.setNeighborhood(request.neighborhood());

        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        user.setRole(Role.ADMIN);

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getPhone(),
                savedUser.getRole(),
                user.getAddress(),
                user.getNum(),
                user.getNeighborhood()
        );
    }
}
