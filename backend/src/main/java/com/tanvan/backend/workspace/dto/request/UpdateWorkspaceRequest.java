// workspace/dto/request/CreateWorkspaceRequest.java
package com.tanvan.backend.workspace.dto.request;

import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWorkspaceRequest {
    
    @Size(min = 2, max = 100, message = "Workspace name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;
}