// workspace/dto/response/WorkspaceMemberResponse.java
package com.tanvan.backend.workspace.dto.response;

import com.tanvan.backend.workspace.entity.WorkspaceRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceMemberResponse {
    private String id;
    private String userId;
    private String username;
    private String fullName;
    private String email;
    private String avatar;
    private WorkspaceRole role;
    private LocalDateTime joinedAt;
}