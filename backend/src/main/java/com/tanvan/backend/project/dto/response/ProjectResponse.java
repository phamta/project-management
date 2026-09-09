// project/dto/response/ProjectResponse.java
package com.tanvan.backend.project.dto.response;

import com.tanvan.backend.project.entity.ProjectStatus;
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
public class ProjectResponse {

    private String id;
    private String workspaceId;
    private String name;
    private String description;
    private ProjectStatus status;
    private String createdBy;
    private String creatorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int memberCount;
    private int taskCount;
    private List<ProjectMemberResponse> members;
}
