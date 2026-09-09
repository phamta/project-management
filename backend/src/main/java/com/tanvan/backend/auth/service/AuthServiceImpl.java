package com.tanvan.backend.auth.service;

import com.tanvan.backend.auth.dto.request.LoginRequest;
import com.tanvan.backend.auth.dto.request.RegisterRequest;
import com.tanvan.backend.auth.dto.response.AuthResponse;
import com.tanvan.backend.auth.dto.response.UserResponse;
import com.tanvan.backend.auth.entity.User;
import com.tanvan.backend.auth.entity.Role;
import com.tanvan.backend.auth.repository.UserRepository;
import com.tanvan.backend.auth.security.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String userId = authentication.getName();
        String token = jwtUtil.generateToken(userId);
        String refreshToken = jwtUtil.generateRefreshToken(userId);

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthResponse(token, refreshToken, mapToUserResponse(user));
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validate username and email
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        // Create user
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .role(Role.MEMBER) // Default role
                .enabled(true)
                .build();

        user = userRepository.save(user);
        String userId = user.getId();

        // Generate tokens
        String token = jwtUtil.generateToken(userId);
        String refreshToken = jwtUtil.generateRefreshToken(userId);

        return new AuthResponse(token, refreshToken, mapToUserResponse(user));
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String userId = jwtUtil.extractUserId(refreshToken);

        String newToken = jwtUtil.generateToken(userId);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthResponse(newToken, newRefreshToken, mapToUserResponse(user));
    }

    @Override
    public void logout(String token) {
        // Implement blacklist or just clear context
        SecurityContextHolder.clearContext();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            String userId = jwtUtil.extractUserId(token);
            return jwtUtil.validateToken(userId);
        } catch (Exception e) {
            return false;
        }
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name());
    }
}