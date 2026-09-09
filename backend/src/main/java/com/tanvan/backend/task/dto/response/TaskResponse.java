// task/dto/response/TaskResponse.java
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
public class TaskResponse {

    private String id;
    private String projectId;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private String assigneeId;
    private String assigneeName;
    private String creatorId;
    private String creatorName;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}
