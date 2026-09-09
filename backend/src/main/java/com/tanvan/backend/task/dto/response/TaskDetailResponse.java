// task/dto/response/TaskDetailResponse.java
package com.tanvan.backend.task.dto.response;

import com.tanvan.backend.task.entity.TaskPriority;
import com.tanvan.backend.task.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDetailResponse {

    private String id;
    private String projectId;
    private String projectName;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private String assigneeId;
    private String assigneeName;
    private String assigneeEmail;
    private String creatorId;
    private String creatorName;
    private String creatorEmail;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}
