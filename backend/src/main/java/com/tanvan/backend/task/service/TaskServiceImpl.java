// task/service/TaskServiceImpl.java
package com.tanvan.backend.task.service;

import com.tanvan.backend.auth.entity.User;
import com.tanvan.backend.auth.repository.UserRepository;
import com.tanvan.backend.common.exception.BusinessException;
import com.tanvan.backend.common.exception.ErrorCode;
import com.tanvan.backend.common.exception.ResourceNotFoundException;
import com.tanvan.backend.project.entity.Project;
import com.tanvan.backend.project.repository.ProjectMemberRepository;
import com.tanvan.backend.project.repository.ProjectRepository;
import com.tanvan.backend.task.dto.request.CreateTaskRequest;
import com.tanvan.backend.task.dto.request.UpdateTaskRequest;
import com.tanvan.backend.task.dto.request.UpdateTaskStatusRequest;
import com.tanvan.backend.task.dto.response.TaskDetailResponse;
import com.tanvan.backend.task.dto.response.TaskResponse;
import com.tanvan.backend.task.entity.Task;
import com.tanvan.backend.task.entity.TaskStatus;
import com.tanvan.backend.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    @Override
    public TaskDetailResponse createTask(String userId, CreateTaskRequest request) {
        // Verify user is a member of the project
        if (!projectMemberRepository.existsByProjectIdAndUserId(request.getProjectId(), userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        Task task = Task.builder()
                .projectId(request.getProjectId())
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? request.getPriority() : com.tanvan.backend.task.entity.TaskPriority.MEDIUM)
                .assigneeId(request.getAssigneeId())
                .creatorId(userId)
                .dueDate(request.getDueDate())
                .build();

        task = taskRepository.save(task);
        log.info("Task created: {} by user {}", task.getTitle(), userId);
        return mapToTaskDetailResponse(task);
    }

    @Override
    public TaskDetailResponse getTask(String taskId, String userId) {
        Task task = findTaskById(taskId);

        if (!projectMemberRepository.existsByProjectIdAndUserId(task.getProjectId(), userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        return mapToTaskDetailResponse(task);
    }

    @Override
    public TaskDetailResponse updateTask(String taskId, String userId, UpdateTaskRequest request) {
        Task task = findTaskById(taskId);

        if (!projectMemberRepository.existsByProjectIdAndUserId(task.getProjectId(), userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getAssigneeId() != null) {
            task.setAssigneeId(request.getAssigneeId());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }

        task = taskRepository.save(task);
        log.info("Task updated: {}", taskId);
        return mapToTaskDetailResponse(task);
    }

    @Override
    public TaskDetailResponse updateTaskStatus(String taskId, String userId, UpdateTaskStatusRequest request) {
        Task task = findTaskById(taskId);

        if (!projectMemberRepository.existsByProjectIdAndUserId(task.getProjectId(), userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        TaskStatus newStatus = request.getStatus();
        TaskStatus oldStatus = task.getStatus();

        // Validate status transition
        if (!isValidTransition(oldStatus, newStatus)) {
            throw new BusinessException(ErrorCode.INVALID_TASK_STATUS);
        }

        task.setStatus(newStatus);

        // Set completedAt when status changes to DONE
        if (newStatus == TaskStatus.DONE) {
            task.setCompletedAt(LocalDateTime.now());
        } else {
            task.setCompletedAt(null);
        }

        task = taskRepository.save(task);
        log.info("Task {} status changed from {} to {}", taskId, oldStatus, newStatus);
        return mapToTaskDetailResponse(task);
    }

    @Override
    public void deleteTask(String taskId, String userId) {
        Task task = findTaskById(taskId);

        // Only task creator or project manager can delete
        if (!task.getCreatorId().equals(userId) &&
                !isProjectManager(task.getProjectId(), userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        taskRepository.delete(task);
        log.info("Task deleted: {}", taskId);
    }

    @Override
    public Page<TaskResponse> getProjectTasks(String projectId, String userId, Pageable pageable) {
        if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        return taskRepository.findByProjectId(projectId, pageable)
                .map(this::mapToTaskResponse);
    }

    @Override
    public Page<TaskResponse> getUserTasks(String userId, Pageable pageable) {
        return taskRepository.findByAssigneeId(userId, pageable)
                .map(this::mapToTaskResponse);
    }

    @Override
    public Page<TaskResponse> getTasksByStatus(String projectId, String userId, TaskStatus status, Pageable pageable) {
        if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        List<Task> tasks = taskRepository.findByProjectIdAndStatus(projectId, status);
        // Since we need pagination over filtered results, use repository query
        return taskRepository.findByProjectId(projectId, pageable)
                .map(this::mapToTaskResponse);
    }

    @Override
    public void assignTask(String taskId, String userId, String assigneeId) {
        Task task = findTaskById(taskId);

        if (!isProjectManager(task.getProjectId(), userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        if (task.getAssigneeId() != null) {
            throw new BusinessException(ErrorCode.TASK_ASSIGNED);
        }

        task.setAssigneeId(assigneeId);
        taskRepository.save(task);
        log.info("Task {} assigned to user {}", taskId, assigneeId);
    }

    @Override
    public long getProjectTaskCount(String projectId) {
        return taskRepository.countByProjectId(projectId);
    }

    @Override
    public long getProjectTaskCountByStatus(String projectId, TaskStatus status) {
        return taskRepository.countByProjectIdAndStatus(projectId, status);
    }

    private boolean isValidTransition(TaskStatus current, TaskStatus next) {
        return switch (current) {
            case TODO -> next == TaskStatus.IN_PROGRESS;
            case IN_PROGRESS -> next == TaskStatus.IN_REVIEW;
            case IN_REVIEW -> next == TaskStatus.DONE;
            case DONE -> false;
        };
    }

    private boolean isProjectManager(String projectId, String userId) {
        return projectMemberRepository.findRoleByProjectIdAndUserId(projectId, userId)
                .map(role -> role == com.tanvan.backend.project.entity.ProjectRole.MANAGER)
                .orElse(false);
    }

    private Task findTaskById(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
    }

    private User findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private Project findProjectById(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
    }

    private TaskResponse mapToTaskResponse(Task task) {
        User creator = findUserById(task.getCreatorId());
        String assigneeName = null;
        if (task.getAssigneeId() != null) {
            try {
                User assignee = findUserById(task.getAssigneeId());
                assigneeName = assignee.getFullName();
            } catch (ResourceNotFoundException e) {
                assigneeName = "Unknown";
            }
        }

        return TaskResponse.builder()
                .id(task.getId())
                .projectId(task.getProjectId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .assigneeId(task.getAssigneeId())
                .assigneeName(assigneeName)
                .creatorId(task.getCreatorId())
                .creatorName(creator.getFullName())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .completedAt(task.getCompletedAt())
                .build();
    }

    private TaskDetailResponse mapToTaskDetailResponse(Task task) {
        Project project = findProjectById(task.getProjectId());
        User creator = findUserById(task.getCreatorId());

        String assigneeName = null;
        String assigneeEmail = null;
        if (task.getAssigneeId() != null) {
            try {
                User assignee = findUserById(task.getAssigneeId());
                assigneeName = assignee.getFullName();
                assigneeEmail = assignee.getEmail();
            } catch (ResourceNotFoundException e) {
                assigneeName = "Unknown";
            }
        }

        return TaskDetailResponse.builder()
                .id(task.getId())
                .projectId(task.getProjectId())
                .projectName(project.getName())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .assigneeId(task.getAssigneeId())
                .assigneeName(assigneeName)
                .assigneeEmail(assigneeEmail)
                .creatorId(task.getCreatorId())
                .creatorName(creator.getFullName())
                .creatorEmail(creator.getEmail())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .completedAt(task.getCompletedAt())
                .build();
    }
}
