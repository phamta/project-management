// comment/controller/CommentController.java
package com.tanvan.backend.comment.controller;

import com.tanvan.backend.common.response.ApiResponse;
import com.tanvan.backend.common.response.PageResponse;
import com.tanvan.backend.comment.dto.request.CreateCommentRequest;
import com.tanvan.backend.comment.dto.request.UpdateCommentRequest;
import com.tanvan.backend.comment.dto.response.CommentResponse;
import com.tanvan.backend.comment.service.CommentService;
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
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @Valid @RequestBody CreateCommentRequest request) {
        String userId = getCurrentUserId();
        CommentResponse response = commentService.createComment(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Comment created successfully", response));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable String commentId,
            @Valid @RequestBody UpdateCommentRequest request) {
        String userId = getCurrentUserId();
        CommentResponse response = commentService.updateComment(commentId, userId, request);
        return ResponseEntity.ok(ApiResponse.success("Comment updated successfully", response));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable String commentId) {
        String userId = getCurrentUserId();
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", null));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<PageResponse<CommentResponse>> getTaskComments(
            @PathVariable String taskId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        String userId = getCurrentUserId();
        Page<CommentResponse> page = commentService.getTaskComments(taskId, userId, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> getComment(@PathVariable String commentId) {
        String userId = getCurrentUserId();
        CommentResponse response = commentService.getComment(commentId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
