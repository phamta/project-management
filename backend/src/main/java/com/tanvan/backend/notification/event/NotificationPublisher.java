package com.tanvan.backend.notification.event;
import com.tanvan.backend.notification.entity.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPublisher {
    private final ApplicationEventPublisher publisher;

    public void publish(String recipientId, String actorId, NotificationType type,
                        String title, String message,
                        String refType, String refId) {
        if (recipientId == null || recipientId.equals(actorId)) return; // không tự thông báo
        publisher.publishEvent(new NotificationEvent(
                recipientId, actorId, type, title, message, refType, refId));
    }
}
