// workspace/dto/response/WorkspaceResponse.java
package com.tanvan.backend.workspace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceResponse {
    private String id;
    private String name;
    private String description;
    private String ownerId;
    private String ownerName;
    private String ownerEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int memberCount;
    private int projectCount;
    private List<WorkspaceMemberResponse> members;
}