// project/service/ProjectServiceImpl.java
package com.tanvan.backend.project.service;

import com.tanvan.backend.auth.entity.User;
import com.tanvan.backend.auth.repository.UserRepository;
import com.tanvan.backend.common.exception.BusinessException;
import com.tanvan.backend.common.exception.ErrorCode;
import com.tanvan.backend.common.exception.ResourceNotFoundException;
import com.tanvan.backend.project.dto.request.AddProjectMemberRequest;
import com.tanvan.backend.project.dto.request.CreateProjectRequest;
import com.tanvan.backend.project.dto.request.UpdateProjectRequest;
import com.tanvan.backend.project.dto.response.ProjectMemberResponse;
import com.tanvan.backend.project.dto.response.ProjectResponse;
import com.tanvan.backend.project.entity.Project;
import com.tanvan.backend.project.entity.ProjectMember;
import com.tanvan.backend.project.entity.ProjectRole;
import com.tanvan.backend.project.repository.ProjectMemberRepository;
import com.tanvan.backend.project.repository.ProjectRepository;
import com.tanvan.backend.task.repository.TaskRepository;
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
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public ProjectResponse createProject(String userId, CreateProjectRequest request) {
        Project project = Project.builder()
                .workspaceId(request.getWorkspaceId())
                .name(request.getName())
                .description(request.getDescription())
                .createdBy(userId)
                .build();

        project = projectRepository.save(project);

        // Add creator as manager
        ProjectMember managerMember = ProjectMember.builder()
                .projectId(project.getId())
                .userId(userId)
                .role(ProjectRole.MANAGER)
                .build();
        projectMemberRepository.save(managerMember);

        log.info("Project created: {} by user {}", project.getName(), userId);
        return mapToProjectResponse(project, userId);
    }

    @Override
    public ProjectResponse getProject(String projectId, String userId) {
        if (!isProjectMember(projectId, userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        Project project = findProjectById(projectId);
        return mapToProjectResponse(project, userId);
    }

    @Override
    public ProjectResponse updateProject(String projectId, String userId, UpdateProjectRequest request) {
        Project project = findProjectById(projectId);

        if (!isProjectManager(projectId, userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        if (request.getName() != null) {
            project.setName(request.getName());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }

        project = projectRepository.save(project);
        log.info("Project updated: {}", projectId);
        return mapToProjectResponse(project, userId);
    }

    @Override
    public void deleteProject(String projectId, String userId) {
        Project project = findProjectById(projectId);

        if (!isProjectManager(projectId, userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        projectMemberRepository.deleteAllByProjectId(projectId);
        projectRepository.delete(project);
        log.info("Project deleted: {}", projectId);
    }

    @Override
    public Page<ProjectResponse> getUserProjects(String userId, Pageable pageable) {
        return projectRepository.findByMemberUserId(userId, pageable)
                .map(project -> mapToProjectResponse(project, userId));
    }

    @Override
    public Page<ProjectResponse> getWorkspaceProjects(String workspaceId, String userId, Pageable pageable) {
        return projectRepository.findByWorkspaceId(workspaceId, pageable)
                .map(project -> mapToProjectResponse(project, userId));
    }

    @Override
    public void addMember(String projectId, String userId, AddProjectMemberRequest request) {
        if (!isProjectManager(projectId, userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, request.getUserId())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_IN_TEAM);
        }

        ProjectMember member = ProjectMember.builder()
                .projectId(projectId)
                .userId(request.getUserId())
                .role(request.getRole())
                .build();

        projectMemberRepository.save(member);
        log.info("User {} added to project {}", request.getUserId(), projectId);
    }

    @Override
    public void removeMember(String projectId, String userId, String memberId) {
        if (!isProjectManager(projectId, userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectMember", "id", memberId));

        if (member.getRole() == ProjectRole.MANAGER) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        projectMemberRepository.delete(member);
        log.info("User {} removed from project {}", memberId, projectId);
    }

    @Override
    public void updateMemberRole(String projectId, String userId, String memberId, ProjectRole role) {
        if (!isProjectManager(projectId, userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectMember", "id", memberId));

        if (member.getRole() == ProjectRole.MANAGER) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        member.setRole(role);
        projectMemberRepository.save(member);
        log.info("Role updated for user {} in project {}", memberId, projectId);
    }

    @Override
    public List<ProjectMemberResponse> getProjectMembers(String projectId, String userId) {
        if (!isProjectMember(projectId, userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        return projectMemberRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToProjectMemberResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isProjectMember(String projectId, String userId) {
        return projectMemberRepository.existsByProjectIdAndUserId(projectId, userId);
    }

    @Override
    public boolean isProjectManager(String projectId, String userId) {
        return projectMemberRepository.findRoleByProjectIdAndUserId(projectId, userId)
                .map(role -> role == ProjectRole.MANAGER)
                .orElse(false);
    }

    private Project findProjectById(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
    }

    private User findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private ProjectResponse mapToProjectResponse(Project project, String userId) {
        List<ProjectMemberResponse> memberResponses = projectMemberRepository
                .findByProjectId(project.getId())
                .stream()
                .map(this::mapToProjectMemberResponse)
                .collect(Collectors.toList());

        User creator = findUserById(project.getCreatedBy());

        long taskCount = taskRepository.countByProjectId(project.getId());

        return ProjectResponse.builder()
                .id(project.getId())
                .workspaceId(project.getWorkspaceId())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus())
                .createdBy(project.getCreatedBy())
                .creatorName(creator.getFullName())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .memberCount(memberResponses.size())
                .taskCount((int) taskCount)
                .members(memberResponses)
                .build();
    }

    private ProjectMemberResponse mapToProjectMemberResponse(ProjectMember member) {
        User user = findUserById(member.getUserId());

        return ProjectMemberResponse.builder()
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
