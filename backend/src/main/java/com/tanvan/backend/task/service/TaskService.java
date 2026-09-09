// task/service/TaskService.java
package com.tanvan.backend.task.service;

import com.tanvan.backend.task.dto.request.CreateTaskRequest;
import com.tanvan.backend.task.dto.request.UpdateTaskRequest;
import com.tanvan.backend.task.dto.request.UpdateTaskStatusRequest;
import com.tanvan.backend.task.dto.response.TaskDetailResponse;
import com.tanvan.backend.task.dto.response.TaskResponse;
import com.tanvan.backend.task.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    TaskDetailResponse createTask(String userId, CreateTaskRequest request);

    TaskDetailResponse getTask(String taskId, String userId);

    TaskDetailResponse updateTask(String taskId, String userId, UpdateTaskRequest request);

    TaskDetailResponse updateTaskStatus(String taskId, String userId, UpdateTaskStatusRequest request);

    void deleteTask(String taskId, String userId);

    Page<TaskResponse> getProjectTasks(String projectId, String userId, Pageable pageable);

    Page<TaskResponse> getUserTasks(String userId, Pageable pageable);

    Page<TaskResponse> getTasksByStatus(String projectId, String userId, TaskStatus status, Pageable pageable);

    void assignTask(String taskId, String userId, String assigneeId);

    long getProjectTaskCount(String projectId);

    long getProjectTaskCountByStatus(String projectId, TaskStatus status);
}
