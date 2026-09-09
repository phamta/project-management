// project/dto/request/AddProjectMemberRequest.java
package com.tanvan.backend.project.dto.request;

import com.tanvan.backend.project.entity.ProjectRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProjectMemberRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Role is required")
    private ProjectRole role;
}
