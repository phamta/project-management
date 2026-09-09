// workspace/controller/WorkspaceController.java
package com.tanvan.backend.workspace.controller;

import com.tanvan.backend.common.response.ApiResponse;
import com.tanvan.backend.common.response.PageResponse;
import com.tanvan.backend.workspace.dto.request.AddMemberRequest;
import com.tanvan.backend.workspace.dto.request.CreateWorkspaceRequest;
import com.tanvan.backend.workspace.dto.request.UpdateWorkspaceRequest;
import com.tanvan.backend.workspace.dto.response.WorkspaceMemberResponse;
import com.tanvan.backend.workspace.dto.response.WorkspaceResponse;
import com.tanvan.backend.workspace.entity.WorkspaceRole;
import com.tanvan.backend.workspace.service.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {
    
    private final WorkspaceService workspaceService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<WorkspaceResponse>> createWorkspace(
            @Valid @RequestBody CreateWorkspaceRequest request) {
        String userId = getCurrentUserId();
        WorkspaceResponse response = workspaceService.createWorkspace(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Workspace created successfully", response));
    }
    
    @GetMapping("/{workspaceId}")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> getWorkspace(
            @PathVariable String workspaceId) {
        String userId = getCurrentUserId();
        WorkspaceResponse response = workspaceService.getWorkspace(workspaceId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/{workspaceId}")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> updateWorkspace(
            @PathVariable String workspaceId,
            @Valid @RequestBody UpdateWorkspaceRequest request) {
        String userId = getCurrentUserId();
        WorkspaceResponse response = workspaceService.updateWorkspace(workspaceId, userId, request);
        return ResponseEntity.ok(ApiResponse.success("Workspace updated successfully", response));
    }
    
    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<ApiResponse<Void>> deleteWorkspace(@PathVariable String workspaceId) {
        String userId = getCurrentUserId();
        workspaceService.deleteWorkspace(workspaceId, userId);
        return ResponseEntity.ok(ApiResponse.success("Workspace deleted successfully", null));
    }
    
    @GetMapping
    public ResponseEntity<PageResponse<WorkspaceResponse>> getUserWorkspaces(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String userId = getCurrentUserId();
        Page<WorkspaceResponse> page = workspaceService.getUserWorkspaces(userId, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }
    
    @PostMapping("/{workspaceId}/members")
    public ResponseEntity<ApiResponse<Void>> addMember(
            @PathVariable String workspaceId,
            @Valid @RequestBody AddMemberRequest request) {
        String userId = getCurrentUserId();
        workspaceService.addMember(workspaceId, userId, request);
        return ResponseEntity.ok(ApiResponse.success("Member added successfully", null));
    }
    
    @DeleteMapping("/{workspaceId}/members/{memberId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable String workspaceId,
            @PathVariable String memberId) {
        String userId = getCurrentUserId();
        workspaceService.removeMember(workspaceId, userId, memberId);
        return ResponseEntity.ok(ApiResponse.success("Member removed successfully", null));
    }
    
    @PutMapping("/{workspaceId}/members/{memberId}/role")
    public ResponseEntity<ApiResponse<Void>> updateMemberRole(
            @PathVariable String workspaceId,
            @PathVariable String memberId,
            @RequestParam WorkspaceRole role) {
        String userId = getCurrentUserId();
        workspaceService.updateMemberRole(workspaceId, userId, memberId, role);
        return ResponseEntity.ok(ApiResponse.success("Member role updated successfully", null));
    }
    
    @GetMapping("/{workspaceId}/members")
    public ResponseEntity<ApiResponse<List<WorkspaceMemberResponse>>> getWorkspaceMembers(
            @PathVariable String workspaceId) {
        String userId = getCurrentUserId();
        List<WorkspaceMemberResponse> members = workspaceService.getWorkspaceMembers(workspaceId, userId);
        return ResponseEntity.ok(ApiResponse.success(members));
    }
    
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // In real implementation, extract user ID from authentication
        // For demo, return a placeholder
        return authentication.getName(); // Assuming the username is the user ID
    }
}