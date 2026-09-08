// auth/dto/request/LoginRequest.java

package com.tanvan.backend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;


public record LoginRequest(
    @NotBlank(message = "Username is required")
    String username,

    @NotBlank(message = "Password is required")
    String password
) {
} 