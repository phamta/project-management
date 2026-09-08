package com.tanvan.backend.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    private String code;
    private String message;
    private int status;
    private String path;
    private LocalDateTime timestamp;
    private Map<String, String> errors;
    
    public static ErrorResponse of(String code, String message, int status, String path) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .status(status)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static ErrorResponse of(String code, String message, int status, String path, Map<String, String> errors) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .status(status)
                .path(path)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}