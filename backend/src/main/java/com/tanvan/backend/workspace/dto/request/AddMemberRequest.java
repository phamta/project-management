// workspace/dto/request/CreateWorkspaceRequest.java
package com.tanvan.backend.workspace.dto.request;

import com.tanvan.backend.workspace.entity.WorkspaceRole;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMemberRequest {
    
    @NotNull(message = "User ID is required")
    private String userId;
    
    @Builder.Default
    private WorkspaceRole role = WorkspaceRole.MEMBER;
}