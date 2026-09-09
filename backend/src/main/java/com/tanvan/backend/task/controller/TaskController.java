// task/controller/TaskController.java
package com.tanvan.backend.task.controller;

import com.tanvan.backend.common.response.ApiResponse;
import com.tanvan.backend.common.response.PageResponse;
import com.tanvan.backend.task.dto.request.CreateTaskRequest;
import com.tanvan.backend.task.dto.request.UpdateTaskRequest;
import com.tanvan.backend.task.dto.request.UpdateTaskStatusRequest;
import com.tanvan.backend.task.dto.response.TaskDetailResponse;
import com.tanvan.backend.task.dto.response.TaskResponse;
import com.tanvan.backend.task.entity.TaskStatus;
import com.tanvan.backend.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<ApiResponse<TaskDetailResponse>> createTask(
            @Valid @RequestBody CreateTaskRequest request) {
        String userId = getCurrentUserId();
        TaskDetailResponse response = taskService.createTask(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Task created successfully", response));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskDetailResponse>> getTask(
            @PathVariable String taskId) {
        String userId = getCurrentUserId();
        TaskDetailResponse response = taskService.getTask(taskId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskDetailResponse>> updateTask(
            @PathVariable String taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
        String userId = getCurrentUserId();
        TaskDetailResponse response = taskService.updateTask(taskId, userId, request);
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", response));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<ApiResponse<TaskDetailResponse>> updateTaskStatus(
            @PathVariable String taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request) {
        String userId = getCurrentUserId();
        TaskDetailResponse response = taskService.updateTaskStatus(taskId, userId, request);
        return ResponseEntity.ok(ApiResponse.success("Task status updated successfully", response));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable String taskId) {
        String userId = getCurrentUserId();
        taskService.deleteTask(taskId, userId);
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully", null));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<PageResponse<TaskResponse>> getProjectTasks(
            @PathVariable String projectId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String userId = getCurrentUserId();
        Page<TaskResponse> page = taskService.getProjectTasks(projectId, userId, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<PageResponse<TaskResponse>> getMyTasks(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String userId = getCurrentUserId();
        Page<TaskResponse> page = taskService.getUserTasks(userId, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @PutMapping("/{taskId}/assign/{assigneeId}")
    public ResponseEntity<ApiResponse<Void>> assignTask(
            @PathVariable String taskId,
            @PathVariable String assigneeId) {
        String userId = getCurrentUserId();
        taskService.assignTask(taskId, userId, assigneeId);
        return ResponseEntity.ok(ApiResponse.success("Task assigned successfully", null));
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
