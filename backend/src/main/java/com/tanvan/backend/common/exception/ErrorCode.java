package com.tanvan.backend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common Errors
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "Internal server error"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "C002", "Bad request"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C003", "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "C004", "Forbidden"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "C005", "Resource not found"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "C006", "Validation error"),
    
    // User Errors
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "User not found"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "U002", "User already exists"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "U003", "Email already exists"),
    INVALID_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "U004", "Invalid current password"),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "U005", "Password mismatch"),
    CANNOT_CHANGE_OWN_ROLE(HttpStatus.FORBIDDEN, "U006", "Cannot change your own role"),
    CANNOT_DISABLE_SELF(HttpStatus.FORBIDDEN, "U007", "Cannot disable your own account"),
    CANNOT_DELETE_SELF(HttpStatus.FORBIDDEN, "U008", "Cannot delete your own account"),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "U009", "Invalid role"),
    USER_INACTIVE(HttpStatus.FORBIDDEN, "U010", "User account is inactive"),
    
    // Auth Errors
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A001", "Invalid credentials"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A002", "Token expired"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "A003", "Invalid token"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A004", "Refresh token expired"),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "A005", "Invalid refresh token"),
    
    // Project Errors
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "Project not found"),
    PROJECT_ALREADY_EXISTS(HttpStatus.CONFLICT, "P002", "Project already exists"),
    PROJECT_FULL(HttpStatus.BAD_REQUEST, "P003", "Project is full"),
    PROJECT_COMPLETED(HttpStatus.BAD_REQUEST, "P004", "Project already completed"),
    PROJECT_CANCELLED(HttpStatus.BAD_REQUEST, "P005", "Project already cancelled"),
    
    // Task Errors
    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "Task not found"),
    TASK_ALREADY_EXISTS(HttpStatus.CONFLICT, "T002", "Task already exists"),
    TASK_COMPLETED(HttpStatus.BAD_REQUEST, "T003", "Task already completed"),
    TASK_OVERDUE(HttpStatus.BAD_REQUEST, "T004", "Task is overdue"),
    INVALID_TASK_STATUS(HttpStatus.BAD_REQUEST, "T005", "Invalid task status transition"),
    TASK_ASSIGNED(HttpStatus.BAD_REQUEST, "T006", "Task already assigned"),
    
    // Team Errors
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "TM001", "Team not found"),
    TEAM_ALREADY_EXISTS(HttpStatus.CONFLICT, "TM002", "Team already exists"),
    TEAM_FULL(HttpStatus.BAD_REQUEST, "TM003", "Team is full"),
    USER_ALREADY_IN_TEAM(HttpStatus.CONFLICT, "TM004", "User already in team"),
    USER_NOT_IN_TEAM(HttpStatus.BAD_REQUEST, "TM005", "User not in team"),
    
    // File Errors
    FILE_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "F001", "File upload error"),
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "F002", "File too large"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "F003", "Invalid file type"),
    
    // Permission Errors
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "PERM001", "Permission denied"),
    INSUFFICIENT_PERMISSIONS(HttpStatus.FORBIDDEN, "PERM002", "Insufficient permissions"),
    
    // Database Errors
    DUPLICATE_ENTRY(HttpStatus.CONFLICT, "DB001", "Duplicate entry"),
    DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT, "DB002", "Data integrity violation");
    
    private final HttpStatus status;
    private final String code;
    private final String message;
}