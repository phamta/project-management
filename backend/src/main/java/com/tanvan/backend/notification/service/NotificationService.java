// notification/service/NotificationService.java
package com.tanvan.backend.notification.service;

import com.tanvan.backend.notification.dto.response.NotificationResponse;
import com.tanvan.backend.notification.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    NotificationResponse createNotification(String userId, NotificationType type, String title, String message, String referenceId, String senderId);

    void markAsRead(String notificationId, String userId);

    void markAllAsRead(String userId);

    Page<NotificationResponse> getUserNotifications(String userId, Pageable pageable);

    Page<NotificationResponse> getUnreadNotifications(String userId, Pageable pageable);

    long getUnreadCount(String userId);

    void deleteNotification(String notificationId, String userId);
}
