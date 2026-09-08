package com.tanvan.backend.user.service;

import com.tanvan.backend.auth.repository.UserRepository;
import com.tanvan.backend.common.exception.BusinessException;
import com.tanvan.backend.common.exception.ErrorCode;
import com.tanvan.backend.common.exception.ResourceNotFoundException;
import com.tanvan.backend.auth.entity.Role;
import com.tanvan.backend.auth.entity.User;
import com.tanvan.backend.user.dto.request.ChangePasswordRequest;
import com.tanvan.backend.user.dto.request.UpdateProfileRequest;
import com.tanvan.backend.user.dto.response.UserListResponse;
import com.tanvan.backend.user.dto.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserProfileResponse getUserProfile(String userId) {
        User user = findUserById(userId);
        return mapToUserProfileResponse(user);
    }
    
    @Override
    public UserProfileResponse updateProfile(String userId, UpdateProfileRequest request) {
        User user = findUserById(userId);
        
        // Check email uniqueness if changed
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(request.getEmail());
        }
        
        // Update fields
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        
        log.info("User profile updated: {}", user.getUsername());
        return mapToUserProfileResponse(user);
    }
    
    @Override
    public void changePassword(String userId, ChangePasswordRequest request) {
        User user = findUserById(userId);
        
        // Validate current password
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }
        
        // Validate new password and confirm password
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("Password changed for user: {}", user.getUsername());
    }
    
    
    @Override
    public void enableUser(String userId) {
        User user = findUserById(userId);
        user.setEnabled(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("User enabled: {}", user.getUsername());
    }
    
    @Override
    public void disableUser(String userId) {
        User user = findUserById(userId);
        
        // Prevent disabling self
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (user.getUsername().equals(currentUsername)) {
            throw new BusinessException(ErrorCode.CANNOT_DISABLE_SELF);
        }
        
        user.setEnabled(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("User disabled: {}", user.getUsername());
    }
    
    @Override
    public void deleteUser(String userId) {
        User user = findUserById(userId);
        
        // Prevent deleting self
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (user.getUsername().equals(currentUsername)) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_SELF);
        }
        
        userRepository.delete(user);
        log.info("User deleted: {}", user.getUsername());
    }
    
    @Override
    public Page<UserListResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::mapToUserListResponse);
    }
    
    @Override
    public Page<UserListResponse> searchUsers(String keyword, Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::mapToUserListResponse);
    }
    
    @Override
    public Page<UserListResponse> getUsersByRole(String role, Pageable pageable) {
        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            Page<User> userPage = userRepository.findByRole(roleEnum, pageable);
            return userPage.map(this::mapToUserListResponse);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_ROLE);
        }
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public long countActiveUsers() {
        return userRepository.findAll()
                .stream()
                .filter(User::isEnabled)
                .count();
    }
    
    @Override
    public long countUsersByRole(Role role) {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == role)
                .count();
    }
    
    private User findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
    
    private UserProfileResponse mapToUserProfileResponse(User user) {
        Set<String> permissions = getPermissionsForRole(user.getRole());
        
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLogin(user.getLastLogin())
                .permissions(permissions)
                .totalProjects(0) // Will be implemented with Project module
                .totalTasks(0)    // Will be implemented with Task module
                .build();
    }
    
    private UserListResponse mapToUserListResponse(User user) {
        return UserListResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }
    
    private Set<String> getPermissionsForRole(Role role) {
        Set<String> permissions = new HashSet<>();
        
        switch (role) {
            case ADMIN:
                permissions.addAll(Set.of(
                    "USER_CREATE", "USER_READ", "USER_UPDATE", "USER_DELETE",
                    "PROJECT_CREATE", "PROJECT_READ", "PROJECT_UPDATE", "PROJECT_DELETE",
                    "TASK_CREATE", "TASK_READ", "TASK_UPDATE", "TASK_DELETE",
                    "TEAM_CREATE", "TEAM_READ", "TEAM_UPDATE", "TEAM_DELETE",
                    "REPORT_VIEW", "REPORT_EXPORT"
                ));
                break;
            case PROJECT_MANAGER:
                permissions.addAll(Set.of(
                    "PROJECT_CREATE", "PROJECT_READ", "PROJECT_UPDATE",
                    "TASK_CREATE", "TASK_READ", "TASK_UPDATE",
                    "TEAM_READ", "TEAM_UPDATE",
                    "REPORT_VIEW"
                ));
                break;
            case TEAM_LEAD:
                permissions.addAll(Set.of(
                    "PROJECT_READ",
                    "TASK_CREATE", "TASK_READ", "TASK_UPDATE",
                    "TEAM_READ"
                ));
                break;
            case MEMBER:
                permissions.addAll(Set.of(
                    "PROJECT_READ",
                    "TASK_READ", "TASK_UPDATE"
                ));
                break;
        }
        
        return permissions;
    }
}