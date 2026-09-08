package com.tanvan.backend.auth.dto.response;

public record AuthResponse(
    String token,
    String refreshToken,
    String type,
    UserResponse user
) {
    public AuthResponse(String token, String refreshToken, UserResponse user) {
        this(token, refreshToken, "Bearer", user);
    }
}
