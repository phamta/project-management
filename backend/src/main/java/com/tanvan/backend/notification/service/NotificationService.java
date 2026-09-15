// notification/service/NotificationService.java
package com.tanvan.backend.notification.service;

import com.tanvan.backend.notification.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tanvan.backend.notification.entity.Notification;
import com.tanvan.backend.notification.event.NotificationEvent;

public interface NotificationService {

    Notification createNotification(NotificationEvent event);

    void markAsRead(String notificationId, String userId);

    void markAllAsRead(String userId);

    Page<NotificationResponse> getUserNotifications(String userId, Pageable pageable);

    Page<NotificationResponse> getUnreadNotifications(String userId, Pageable pageable);

    long getUnreadCount(String userId);

    void deleteNotification(String notificationId, String userId);
}
