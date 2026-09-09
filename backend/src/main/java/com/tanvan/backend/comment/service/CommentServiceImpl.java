// comment/service/CommentServiceImpl.java
package com.tanvan.backend.comment.service;

import com.tanvan.backend.auth.entity.User;
import com.tanvan.backend.auth.repository.UserRepository;
import com.tanvan.backend.common.exception.BusinessException;
import com.tanvan.backend.common.exception.ErrorCode;
import com.tanvan.backend.common.exception.ResourceNotFoundException;
import com.tanvan.backend.comment.dto.request.CreateCommentRequest;
import com.tanvan.backend.comment.dto.request.UpdateCommentRequest;
import com.tanvan.backend.comment.dto.response.CommentResponse;
import com.tanvan.backend.comment.entity.Comment;
import com.tanvan.backend.comment.repository.CommentRepository;
import com.tanvan.backend.project.repository.ProjectMemberRepository;
import com.tanvan.backend.task.entity.Task;
import com.tanvan.backend.task.repository.TaskRepository;
import com.tanvan.backend.websocket.controller.WebSocketController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final WebSocketController webSocketController;

    @Override
    public CommentResponse createComment(String userId, CreateCommentRequest request) {
        Task task = findTaskById(request.getTaskId());

        if (!projectMemberRepository.existsByProjectIdAndUserId(task.getProjectId(), userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        Comment comment = Comment.builder()
                .taskId(request.getTaskId())
                .userId(userId)
                .content(request.getContent())
                .parentId(request.getParentId())
                .build();

        comment = commentRepository.save(comment);
        log.info("Comment created on task {} by user {}", request.getTaskId(), userId);
        return mapToCommentResponse(comment);
    }

    @Override
    public CommentResponse updateComment(String commentId, String userId, UpdateCommentRequest request) {
        Comment comment = findCommentById(commentId);

        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        comment.setContent(request.getContent());
        comment = commentRepository.save(comment);
        log.info("Comment updated: {}", commentId);
        return mapToCommentResponse(comment);
    }

    @Override
    public void deleteComment(String commentId, String userId) {
        Comment comment = findCommentById(commentId);

        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        commentRepository.softDeleteById(commentId);
        log.info("Comment deleted: {} by user {}", commentId, userId);
    }

    @Override
    public Page<CommentResponse> getTaskComments(String taskId, String userId, Pageable pageable) {
        Task task = findTaskById(taskId);

        if (!projectMemberRepository.existsByProjectIdAndUserId(task.getProjectId(), userId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        return commentRepository.findByTaskIdAndDeletedFalseOrderByCreatedAtDesc(taskId, pageable)
                .map(this::mapToCommentResponse);
    }

    @Override
    public CommentResponse getComment(String commentId, String userId) {
        Comment comment = findCommentById(commentId);
        return mapToCommentResponse(comment);
    }

    private Comment findCommentById(String commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
    }

    private Task findTaskById(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
    }

    private User findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        User user = findUserById(comment.getUserId());

        return CommentResponse.builder()
                .id(comment.getId())
                .taskId(comment.getTaskId())
                .userId(comment.getUserId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .content(comment.getContent())
                .parentId(comment.getParentId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .deleted(comment.getDeleted())
                .build();
    }
}
