// comment/service/CommentService.java
package com.tanvan.backend.comment.service;

import com.tanvan.backend.comment.dto.request.CreateCommentRequest;
import com.tanvan.backend.comment.dto.request.UpdateCommentRequest;
import com.tanvan.backend.comment.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    CommentResponse createComment(String userId, CreateCommentRequest request);

    CommentResponse updateComment(String commentId, String userId, UpdateCommentRequest request);

    void deleteComment(String commentId, String userId);

    Page<CommentResponse> getTaskComments(String taskId, String userId, Pageable pageable);

    CommentResponse getComment(String commentId, String userId);
}
