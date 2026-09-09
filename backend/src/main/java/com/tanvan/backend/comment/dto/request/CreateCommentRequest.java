// comment/dto/request/CreateCommentRequest.java
package com.tanvan.backend.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {

    @NotBlank(message = "Task ID is required")
    private String taskId;

    @NotBlank(message = "Comment content is required")
    @Size(max = 5000, message = "Comment must not exceed 5000 characters")
    private String content;

    private String parentId;
}
