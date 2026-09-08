package com.tanvan.backend.user.dto.response;

import com.tanvan.backend.auth.entity.Role;

import lombok.Setter;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter 
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserProfileResponse {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;
    private Set<String> permissions;
    private long totalProjects;
    private long totalTasks;

    
}