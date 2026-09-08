package com.tanvan.backend.user.service;

import com.tanvan.backend.auth.entity.Role;

import com.tanvan.backend.user.dto.request.ChangePasswordRequest;
import com.tanvan.backend.user.dto.request.UpdateProfileRequest;
import com.tanvan.backend.user.dto.response.UserListResponse;
import com.tanvan.backend.user.dto.response.UserProfileResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserProfileResponse getUserProfile(String userId);
    
    UserProfileResponse updateProfile(String userId, UpdateProfileRequest request);
    
    void changePassword(String userId, ChangePasswordRequest request);
        
    void enableUser(String userId);
    
    void disableUser(String userId);
    
    void deleteUser(String userId);
    
    Page<UserListResponse> getAllUsers(Pageable pageable);
    
    Page<UserListResponse> searchUsers(String keyword, Pageable pageable);
    
    Page<UserListResponse> getUsersByRole(String role, Pageable pageable);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    long countActiveUsers();
    
    long countUsersByRole(Role role);    
}
