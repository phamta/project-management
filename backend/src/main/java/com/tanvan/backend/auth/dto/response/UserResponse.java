// auth/dto/response/UserResponse.java

package com.tanvan.backend.auth.dto.response;

public record UserResponse(
    String id,
    String username,
    String email,
    String role
) {
}
