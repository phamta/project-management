// notification/service/NotificationServiceImpl.java
package com.tanvan.backend.notification.service;

import com.tanvan.backend.common.exception.BusinessException;
import com.tanvan.backend.common.exception.ErrorCode;
import com.tanvan.backend.common.exception.ResourceNotFoundException;
import com.tanvan.backend.notification.dto.response.NotificationResponse;
import com.tanvan.backend.notification.entity.Notification;
import com.tanvan.backend.notification.entity.NotificationType;
import com.tanvan.backend.notification.repository.NotificationRepository;
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
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final WebSocketController webSocketController;

    @Override
    public NotificationResponse createNotification(String userId, NotificationType type, String title,
                                                   String message, String referenceId, String senderId) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .message(message)
                .referenceId(referenceId)
                .senderId(senderId)
                .build();

        notification = notificationRepository.save(notification);

        // Send real-time notification via WebSocket
        NotificationResponse response = mapToNotificationResponse(notification);
        webSocketController.sendNotification(userId, response);

        log.info("Notification created for user {}: {}", userId, title);
        return response;
    }

    @Override
    public void markAsRead(String notificationId, String userId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        notificationRepository.markAsRead(notificationId, userId);
        log.info("Notification {} marked as read by user {}", notificationId, userId);
    }

    @Override
    public void markAllAsRead(String userId) {
        notificationRepository.markAllAsRead(userId);
        log.info("All notifications marked as read for user {}", userId);
    }

    @Override
    public Page<NotificationResponse> getUserNotifications(String userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToNotificationResponse);
    }

    @Override
    public Page<NotificationResponse> getUnreadNotifications(String userId, Pageable pageable) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToNotificationResponse);
    }

    @Override
    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    public void deleteNotification(String notificationId, String userId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        notificationRepository.delete(notification);
        log.info("Notification {} deleted by user {}", notificationId, userId);
    }

    private NotificationResponse mapToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .referenceId(notification.getReferenceId())
                .senderId(notification.getSenderId())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
