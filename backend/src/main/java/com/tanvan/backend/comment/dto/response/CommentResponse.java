// comment/dto/response/CommentResponse.java
package com.tanvan.backend.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private String id;
    private String taskId;
    private String userId;
    private String username;
    private String fullName;
    private String content;
    private String parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
}
