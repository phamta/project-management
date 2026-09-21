package com.tanvan.backend.notification.event;

import com.tanvan.backend.notification.dto.response.NotificationResponse;
import com.tanvan.backend.notification.entity.Notification;
import com.tanvan.backend.notification.service.NotificationServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationServiceImpl notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Async
    @EventListener
    @Transactional
    public void handle(NotificationEvent event) {
        Notification saved = notificationService.createNotification(event);

        // Gửi realtime tới user cụ thể
        messagingTemplate.convertAndSendToUser(
                event.getRecipientId().toString(),
                "/queue/notifications",
                NotificationResponse.from(saved)
        );
        log.debug("Notification sent to user {}", event.getRecipientId());
    }
}