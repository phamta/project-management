// task/dto/request/UpdateTaskRequest.java
package com.tanvan.backend.task.dto.request;

import com.tanvan.backend.task.entity.TaskPriority;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskRequest {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    private TaskPriority priority;

    private String assigneeId;

    private LocalDateTime dueDate;
}
