// workspace/service/WorkspaceService.java
package com.tanvan.backend.workspace.service;

import com.tanvan.backend.workspace.dto.request.AddMemberRequest;
import com.tanvan.backend.workspace.dto.request.CreateWorkspaceRequest;
import com.tanvan.backend.workspace.dto.request.UpdateWorkspaceRequest;
import com.tanvan.backend.workspace.dto.response.WorkspaceMemberResponse;
import com.tanvan.backend.workspace.dto.response.WorkspaceResponse;
import com.tanvan.backend.workspace.entity.WorkspaceRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WorkspaceService {
    
    WorkspaceResponse createWorkspace(String userId, CreateWorkspaceRequest request);
    
    WorkspaceResponse getWorkspace(String workspaceId, String userId);
    
    WorkspaceResponse updateWorkspace(String workspaceId, String userId, UpdateWorkspaceRequest request);
    
    void deleteWorkspace(String workspaceId, String userId);
    
    Page<WorkspaceResponse> getUserWorkspaces(String userId, Pageable pageable);
    
    void addMember(String workspaceId, String userId, AddMemberRequest request);
    
    void removeMember(String workspaceId, String userId, String memberId);
    
    void updateMemberRole(String workspaceId, String userId, String memberId, WorkspaceRole role);
    
    List<WorkspaceMemberResponse> getWorkspaceMembers(String workspaceId, String userId);
    
    boolean isWorkspaceMember(String workspaceId, String userId);
    
    boolean isWorkspaceOwner(String workspaceId, String userId);
}