package com.tanvan.backend.auth.controller;

import com.tanvan.backend.auth.dto.response.UserResponse;
import com.tanvan.backend.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Cognito owns the login, refresh and logout session.
     * The client must clear its Cognito tokens or call Cognito global sign-out.
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of(
                "message", "Clear the Cognito session on the client"));
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validateToken(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
                "valid", authentication != null && authentication.isAuthenticated()));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(authService.getCurrentUser(jwt));
    }
}
