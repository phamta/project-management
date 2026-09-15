// notification/service/NotificationServiceImpl.java
package com.tanvan.backend.notification.service;

import com.tanvan.backend.common.exception.ResourceNotFoundException;
import com.tanvan.backend.auth.repository.UserRepository;
import com.tanvan.backend.notification.dto.response.NotificationResponse;
import com.tanvan.backend.notification.entity.Notification;
import com.tanvan.backend.notification.event.NotificationEvent;
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
    private final UserRepository userRepository;

    @Override
    public Notification createNotification(NotificationEvent e) {
        Notification notification = Notification.builder()
                .recipient(userRepository.getReferenceById(e.getRecipientId()))
                .actor(e.getActorId() != null ? userRepository.getReferenceById(e.getActorId()) : null)
                .type(e.getType())
                .title(e.getTitle())
                .message(e.getMessage())
                .referenceType(e.getReferenceType())
                .referenceId(e.getReferenceId())
                .isRead(false)
                .build();

        notification = notificationRepository.save(notification);

        // Send real-time notification via WebSocket
        NotificationResponse response = mapToNotificationResponse(notification);
        webSocketController.sendNotification(e.getRecipientId(), response);

        log.info("Notification created for user {}: {}", e.getRecipientId(), e.getTitle());
        return notification;
    }

    @Override
    public void markAsRead(String notificationId, String userId) {
        Notification notification = notificationRepository.findByIdAndRecipient_Id(notificationId, userId)
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
        return notificationRepository.findByRecipient_IdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToNotificationResponse);
    }

    @Override
    public Page<NotificationResponse> getUnreadNotifications(String userId, Pageable pageable) {
        return notificationRepository.findByRecipient_IdAndIsReadFalseOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToNotificationResponse);
    }

    @Override
    public long getUnreadCount(String userId) {
        return notificationRepository.countByRecipient_IdAndIsReadFalse(userId);
    }

    @Override
    public void deleteNotification(String notificationId, String userId) {
        Notification notification = notificationRepository.findByIdAndRecipient_Id(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        notificationRepository.delete(notification);
        log.info("Notification {} deleted by user {}", notificationId, userId);
    }

    private NotificationResponse mapToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getActor().getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .referenceId(notification.getReferenceId())
                .senderId(notification.getRecipient().getId())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
