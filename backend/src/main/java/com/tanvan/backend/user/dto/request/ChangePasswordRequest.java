package com.tanvan.backend.user.dto.request;

public record ChangePasswordRequest(
    String currentPassword,
    String newPassword,
    String confirmPassword
) {
    
}
