// project/controller/ProjectController.java
package com.tanvan.backend.project.controller;

import com.tanvan.backend.common.response.ApiResponse;
import com.tanvan.backend.common.response.PageResponse;
import com.tanvan.backend.project.dto.request.AddProjectMemberRequest;
import com.tanvan.backend.project.dto.request.CreateProjectRequest;
import com.tanvan.backend.project.dto.request.UpdateProjectRequest;
import com.tanvan.backend.project.dto.response.ProjectMemberResponse;
import com.tanvan.backend.project.dto.response.ProjectResponse;
import com.tanvan.backend.project.entity.ProjectRole;
import com.tanvan.backend.project.service.ProjectService;
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
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody CreateProjectRequest request) {
        String userId = getCurrentUserId();
        ProjectResponse response = projectService.createProject(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project created successfully", response));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(
            @PathVariable String projectId) {
        String userId = getCurrentUserId();
        ProjectResponse response = projectService.getProject(projectId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable String projectId,
            @Valid @RequestBody UpdateProjectRequest request) {
        String userId = getCurrentUserId();
        ProjectResponse response = projectService.updateProject(projectId, userId, request);
        return ResponseEntity.ok(ApiResponse.success("Project updated successfully", response));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable String projectId) {
        String userId = getCurrentUserId();
        projectService.deleteProject(projectId, userId);
        return ResponseEntity.ok(ApiResponse.success("Project deleted successfully", null));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProjectResponse>> getUserProjects(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String userId = getCurrentUserId();
        Page<ProjectResponse> page = projectService.getUserProjects(userId, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @PostMapping("/{projectId}/members")
    public ResponseEntity<ApiResponse<Void>> addMember(
            @PathVariable String projectId,
            @Valid @RequestBody AddProjectMemberRequest request) {
        String userId = getCurrentUserId();
        projectService.addMember(projectId, userId, request);
        return ResponseEntity.ok(ApiResponse.success("Member added successfully", null));
    }

    @DeleteMapping("/{projectId}/members/{memberId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable String projectId,
            @PathVariable String memberId) {
        String userId = getCurrentUserId();
        projectService.removeMember(projectId, userId, memberId);
        return ResponseEntity.ok(ApiResponse.success("Member removed successfully", null));
    }

    @PutMapping("/{projectId}/members/{memberId}/role")
    public ResponseEntity<ApiResponse<Void>> updateMemberRole(
            @PathVariable String projectId,
            @PathVariable String memberId,
            @RequestParam ProjectRole role) {
        String userId = getCurrentUserId();
        projectService.updateMemberRole(projectId, userId, memberId, role);
        return ResponseEntity.ok(ApiResponse.success("Member role updated successfully", null));
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<ApiResponse<List<ProjectMemberResponse>>> getProjectMembers(
            @PathVariable String projectId) {
        String userId = getCurrentUserId();
        List<ProjectMemberResponse> members = projectService.getProjectMembers(projectId, userId);
        return ResponseEntity.ok(ApiResponse.success(members));
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
