package com.tanvan.backend.user.controller;

import com.tanvan.backend.common.response.ApiResponse;
import com.tanvan.backend.common.response.PageResponse;
import com.tanvan.backend.user.dto.request.ChangePasswordRequest;
import com.tanvan.backend.user.dto.request.UpdateProfileRequest;
import com.tanvan.backend.user.dto.response.UserListResponse;
import com.tanvan.backend.user.dto.response.UserProfileResponse;
import com.tanvan.backend.user.service.UserService;
import com.tanvan.backend.auth.entity.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        // Get user by userId
        return ResponseEntity.ok(ApiResponse.success(userService.getUserProfile(userId)));
    }
    
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasAccess(#userId)")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(@PathVariable String userId) {
        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        // Get user by userId and update
        UserProfileResponse response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        // Get user by userId and change password
        userService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }
    
    @PutMapping("/{userId}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> enableUser(@PathVariable String userId) {
        userService.enableUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User enabled successfully", null));
    }
    
    @PutMapping("/{userId}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> disableUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        userService.disableUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User disabled successfully", null));
    }
    
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    public ResponseEntity<PageResponse<UserListResponse>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UserListResponse> page = userService.getAllUsers(pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    public ResponseEntity<PageResponse<UserListResponse>> searchUsers(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserListResponse> page = userService.searchUsers(keyword, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }
    
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    public ResponseEntity<PageResponse<UserListResponse>> getUsersByRole(
            @PathVariable String role,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserListResponse> page = userService.getUsersByRole(role, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getUserStats() {
        long total = userService.countActiveUsers();
        long adminCount = userService.countUsersByRole(Role.ADMIN);
        long pmCount = userService.countUsersByRole(Role.PROJECT_MANAGER);
        long teamLeadCount = userService.countUsersByRole(Role.TEAM_LEAD);
        long memberCount = userService.countUsersByRole(Role.MEMBER);
        
        var stats = java.util.Map.of(
            "totalActive", total,
            "byRole", java.util.Map.of(
                "ADMIN", adminCount,
                "PROJECT_MANAGER", pmCount,
                "TEAM_LEAD", teamLeadCount,
                "MEMBER", memberCount
            )
        );
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}