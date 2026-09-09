// project/service/ProjectService.java
package com.tanvan.backend.project.service;

import com.tanvan.backend.project.dto.request.AddProjectMemberRequest;
import com.tanvan.backend.project.dto.request.CreateProjectRequest;
import com.tanvan.backend.project.dto.request.UpdateProjectRequest;
import com.tanvan.backend.project.dto.response.ProjectMemberResponse;
import com.tanvan.backend.project.dto.response.ProjectResponse;
import com.tanvan.backend.project.entity.ProjectRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectService {

    ProjectResponse createProject(String userId, CreateProjectRequest request);

    ProjectResponse getProject(String projectId, String userId);

    ProjectResponse updateProject(String projectId, String userId, UpdateProjectRequest request);

    void deleteProject(String projectId, String userId);

    Page<ProjectResponse> getUserProjects(String userId, Pageable pageable);

    Page<ProjectResponse> getWorkspaceProjects(String workspaceId, String userId, Pageable pageable);

    void addMember(String projectId, String userId, AddProjectMemberRequest request);

    void removeMember(String projectId, String userId, String memberId);

    void updateMemberRole(String projectId, String userId, String memberId, ProjectRole role);

    List<ProjectMemberResponse> getProjectMembers(String projectId, String userId);

    boolean isProjectMember(String projectId, String userId);

    boolean isProjectManager(String projectId, String userId);
}
