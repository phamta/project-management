package com.tanvan.backend.auth.service;

import org.springframework.security.oauth2.jwt.Jwt;

import com.tanvan.backend.auth.dto.response.UserResponse;

public interface AuthService {
    UserResponse getCurrentUser(Jwt jwt);
}