package com.tanvan.backend.notification.event;

import com.tanvan.backend.notification.entity.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor
public class NotificationEvent {
    private final String recipientId;
    private final String actorId;
    private final NotificationType type;
    private final String title;
    private final String message;
    private final String referenceType;
    private final String referenceId;
}
