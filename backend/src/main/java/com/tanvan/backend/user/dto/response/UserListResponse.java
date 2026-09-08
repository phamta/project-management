package com.tanvan.backend.user.dto.response;

import com.tanvan.backend.auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponse{
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String avatar;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
