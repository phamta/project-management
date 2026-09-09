// project/dto/response/ProjectMemberResponse.java
package com.tanvan.backend.project.dto.response;

import com.tanvan.backend.project.entity.ProjectRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberResponse {

    private String id;
    private String userId;
    private String username;
    private String fullName;
    private String email;
    private ProjectRole role;
    private LocalDateTime joinedAt;
}
