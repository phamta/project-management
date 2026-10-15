package com.tanvan.backend.auth.service;

import com.tanvan.backend.auth.dto.response.UserResponse;
import com.tanvan.backend.auth.entity.User;
import com.tanvan.backend.auth.entity.Role;
import com.tanvan.backend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getCurrentUser(Jwt jwt) {
        String sub      = jwt.getSubject();                               // "sub"
        String username = jwt.getClaimAsString("cognito:username");       // "alice"
        String email    = jwt.getClaimAsString("email");                  // "alice@example.com"
        String fullName = jwt.getClaimAsString("custom:fullName");        // "Alice Nguyen"
        // KHÔNG có role trong JWT → mặc định hoặc dùng "cognito:groups"
        String role     = extractRole(jwt);

        User user = userRepository.findById(sub)
                .map(existing -> {
                    existing.setUsername(username);
                    existing.setEmail(email);
                    existing.setFullName(fullName);
                    return existing;
                })
                .orElseGet(() -> User.builder()
                        .id(sub)
                        .username(username)
                        .email(email)
                        .fullName(fullName)
                        .password("COGNITO_MANAGED")   // ← FIX: placeholder
                        .role(Role.valueOf(role))       // ← FIX: role không được null
                        .enabled(true)
                        .build());

        userRepository.save(user);

        return new UserResponse(
                sub,
                username,
                email,
                role,
                null // avatar - không có trong JWT, để null hoặc thêm custom attribute
        );
    }

    private String extractRole(Jwt jwt) {
        // Nếu bạn cấu hình Cognito Groups thì JWT có claim "cognito:groups"
        var groups = jwt.getClaimAsStringList("cognito:groups");
        if (groups != null && !groups.isEmpty()) {
            return groups.get(0); // ví dụ: "ADMIN" hoặc "USER"
        }
        return "USER";
    }
}
