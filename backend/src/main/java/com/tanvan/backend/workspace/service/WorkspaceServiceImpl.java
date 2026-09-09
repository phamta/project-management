// workspace/service/WorkspaceServiceImpl.java
package com.tanvan.backend.workspace.service;

import com.tanvan.backend.common.exception.BusinessException;
import com.tanvan.backend.common.exception.ErrorCode;
import com.tanvan.backend.common.exception.ResourceNotFoundException;
import com.tanvan.backend.auth.entity.User;
import com.tanvan.backend.auth.repository.UserRepository;
import com.tanvan.backend.workspace.dto.request.AddMemberRequest;
import com.tanvan.backend.workspace.dto.request.CreateWorkspaceRequest;
import com.tanvan.backend.workspace.dto.request.UpdateWorkspaceRequest;
import com.tanvan.backend.workspace.dto.response.WorkspaceMemberResponse;
import com.tanvan.backend.workspace.dto.response.WorkspaceResponse;
import com.tanvan.backend.workspace.entity.Workspace;
import com.tanvan.backend.workspace.entity.WorkspaceMember;
import com.tanvan.backend.workspace.entity.WorkspaceRole;
import com.tanvan.backend.workspace.repository.WorkspaceMemberRepository;
import com.tanvan.backend.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WorkspaceServiceImpl implements WorkspaceService {
    
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;
    
    @Override
    public WorkspaceResponse createWorkspace(String userId, CreateWorkspaceRequest request) {
        // Create workspace
        Workspace workspace = Workspace.builder()
                .name(request.getName())
                .description(request.getDescription())
                .ownerId(userId)
                .build();
        
        workspace = workspaceRepository.save(workspace);
        
        // Add owner as member with OWNER role
        WorkspaceMember ownerMember = WorkspaceMember.builder()
                .workspaceId(workspace.getId())
                .userId(userId)
                .role(WorkspaceRole.OWNER)
                .build();
        workspaceMemberRepository.save(ownerMember);
        
        log.info("Workspace created: {} by user {}", workspace.getName(), userId);
        return mapToWorkspaceResponse(workspace, userId);
    }
    
    @Override
    public WorkspaceResponse getWorkspace(String workspaceId, String userId) {
        // Check access
        if (!isWorkspaceMember(workspaceId, userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }
        
        Workspace workspace = findWorkspaceById(workspaceId);
        return mapToWorkspaceResponse(workspace, userId);
    }
    
    @Override
    public WorkspaceResponse updateWorkspace(String workspaceId, String userId, UpdateWorkspaceRequest request) {
        Workspace workspace = findWorkspaceById(workspaceId);
        
        // Check permission (only owner or admin can update)
        if (!isWorkspaceOwner(workspaceId, userId) && !isWorkspaceAdmin(workspaceId, userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }
        
        if (request.getName() != null) {
            workspace.setName(request.getName());
        }
        if (request.getDescription() != null) {
            workspace.setDescription(request.getDescription());
        }
        
        workspace = workspaceRepository.save(workspace);
        log.info("Workspace updated: {}", workspaceId);
        return mapToWorkspaceResponse(workspace, userId);
    }
    
    @Override
    public void deleteWorkspace(String workspaceId, String userId) {
        Workspace workspace = findWorkspaceById(workspaceId);
        
        // Only owner can delete
        if (!isWorkspaceOwner(workspaceId, userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }
        
        // Delete all members first
        workspaceMemberRepository.deleteAllByWorkspaceId(workspaceId);
        
        // Delete workspace
        workspaceRepository.delete(workspace);
        log.info("Workspace deleted: {}", workspaceId);
    }
    
    @Override
    public Page<WorkspaceResponse> getUserWorkspaces(String userId, Pageable pageable) {
        return workspaceRepository.findByMemberUserId(userId, pageable)
                .map(workspace -> mapToWorkspaceResponse(workspace, userId));
    }
    
    @Override
    public void addMember(String workspaceId, String userId, AddMemberRequest request) {
        // Check permission (owner or admin can add members)
        if (!isWorkspaceOwner(workspaceId, userId) && !isWorkspaceAdmin(workspaceId, userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }
        
        // Check if already a member
        if (workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, request.getUserId())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_IN_TEAM);
        }
        
        // Add member
        WorkspaceMember workspaceMember = WorkspaceMember.builder()
                .workspaceId(workspaceId)
                .userId(request.getUserId())
                .role(request.getRole())
                .build();
        
        workspaceMemberRepository.save(workspaceMember);
        log.info("User {} added to workspace {}", request.getUserId(), workspaceId);
    }
    
    @Override
    public void removeMember(String workspaceId, String userId, String memberId) {
        // Check permission
        if (!isWorkspaceOwner(workspaceId, userId) && !isWorkspaceAdmin(workspaceId, userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }
        
        // Can't remove owner
        WorkspaceMember member = workspaceMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", memberId));
        
        if (member.getRole() == WorkspaceRole.OWNER) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }
        
        workspaceMemberRepository.delete(member);
        log.info("User {} removed from workspace {}", memberId, workspaceId);
    }
    
    @Override
    public void updateMemberRole(String workspaceId, String userId, String memberId, WorkspaceRole role) {
        // Only owner can update roles
        if (!isWorkspaceOwner(workspaceId, userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }
        
        WorkspaceMember member = workspaceMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", memberId));
        
        // Can't change owner's role
        if (member.getRole() == WorkspaceRole.OWNER) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }
        
        member.setRole(role);
        workspaceMemberRepository.save(member);
        log.info("Role updated for user {} in workspace {}", memberId, workspaceId);
    }
    
    @Override
    public List<WorkspaceMemberResponse> getWorkspaceMembers(String workspaceId, String userId) {
        if (!isWorkspaceMember(workspaceId, userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }
        
        return workspaceMemberRepository.findByWorkspaceId(workspaceId)
                .stream()
                .map(this::mapToWorkspaceMemberResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isWorkspaceMember(String workspaceId, String userId) {
        return workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId);
    }
    
    @Override
    public boolean isWorkspaceOwner(String workspaceId, String userId) {
        return workspaceRepository.isOwner(workspaceId, userId);
    }
    
    private boolean isWorkspaceAdmin(String workspaceId, String userId) {
        return workspaceMemberRepository.findRoleByWorkspaceIdAndUserId(workspaceId, userId)
                .map(role -> role == WorkspaceRole.ADMIN)
                .orElse(false);
    }
    
    private Workspace findWorkspaceById(String workspaceId) {
        return workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", workspaceId));
    }
    
    private User findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
    
    private WorkspaceResponse mapToWorkspaceResponse(Workspace workspace, String userId) {
        List<WorkspaceMemberResponse> memberResponses = workspaceMemberRepository
                .findByWorkspaceId(workspace.getId())
                .stream()
                .map(this::mapToWorkspaceMemberResponse)
                .collect(Collectors.toList());
        
        User owner = findUserById(workspace.getOwnerId());
        
        return WorkspaceResponse.builder()
                .id(workspace.getId())
                .name(workspace.getName())
                .description(workspace.getDescription())
                .ownerId(workspace.getOwnerId())
                .ownerName(owner.getFullName())
                .createdAt(workspace.getCreatedAt())
                .updatedAt(workspace.getUpdatedAt())
                .memberCount(memberResponses.size())
                .projectCount(0) // Will be implemented with Project module
                .members(memberResponses)
                .build();
    }
    
    private WorkspaceMemberResponse mapToWorkspaceMemberResponse(WorkspaceMember member) {
        User user = findUserById(member.getUserId());
        
        return WorkspaceMemberResponse.builder()
                .id(member.getId())
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}