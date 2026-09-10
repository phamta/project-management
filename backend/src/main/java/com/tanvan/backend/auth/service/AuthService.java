package com.tanvan.backend.auth.service;

import com.tanvan.backend.auth.dto.response.UserResponse;

import com.tanvan.backend.auth.dto.request.LoginRequest;
import com.tanvan.backend.auth.dto.request.RegisterRequest;
import com.tanvan.backend.auth.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout(String token);
    boolean validateToken(String token);
    UserResponse getCurrentUser(String token);
}