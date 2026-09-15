// notification/dto/response/NotificationResponse.java
package com.tanvan.backend.notification.dto.response;

import com.tanvan.backend.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private String id;
    private String userId;
    private NotificationType type;
    private String title;
    private String message;
    private String referenceId;
    private String senderId;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponse from(com.tanvan.backend.notification.entity.Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getActor() != null ? notification.getActor().getId() : null)
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .referenceId(notification.getReferenceId())
                .senderId(notification.getRecipient() != null ? notification.getRecipient().getId() : null)
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
