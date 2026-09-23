package com.tanvan.backend.notification.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.tanvan.backend.notification.mail.service.MailServiceImpl;
import com.tanvan.backend.notification.entity.NotificationType;
import com.tanvan.backend.notification.event.NotificationEvent;
import com.tanvan.backend.auth.repository.UserRepository;
import com.tanvan.backend.auth.entity.User;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationListener {

    private final MailServiceImpl mailService;
    private final UserRepository userRepository;
    // hoặc UserService nếu có method lấy email

    @Async("mailExecutor")
    @EventListener
    public void onNotification(NotificationEvent event) {
        try {
            // Lấy email người nhận
            User recipient = userRepository.findById(event.getRecipientId())
                .orElse(null);
            if (recipient == null || recipient.getEmail() == null) return;

            String actorName = userRepository.findById(event.getActorId())
                .map(User::getFullName)
                .orElse("Someone");

            // Chuẩn bị biến cho template
            Map<String, Object> vars = Map.of(
                "recipientName", recipient.getFullName(),
                "actorName",     actorName,
                "message",       event.getMessage(),
                "actionUrl",     buildActionUrl(event),
                "notificationType", event.getType().name()
            );

            mailService.sendHtml(
                recipient.getEmail(),
                buildSubject(event),
                resolveTemplate(event.getType()),
                vars
            );
        } catch (Exception e) {
            log.error("Email listener failed for event {}", event, e);
        }
    }

    private String resolveTemplate(NotificationType type) {
        return switch (type) {
            case TASK_ASSIGNED   -> "task-assigned";
            case COMMENT_ADDED   -> "comment-added";
            case PROJECT_INVITE  -> "project-invite";
            case WORKSPACE_INVITE-> "workspace-invite";
            case MENTION         -> "mention";
            default              -> "generic";
        };
    }

    private String buildSubject(NotificationEvent event) {
        return switch (event.getType()) {
            case TASK_ASSIGNED -> "[CollabFlow] Bạn được giao một task mới";
            case COMMENT_ADDED -> "[CollabFlow] Có bình luận mới";
            default            -> "[CollabFlow] Thông báo mới";
        };
    }

    private String buildActionUrl(NotificationEvent event) {
        return "http://localhost:5173/tasks/" + event.getReferenceId(); // Giả sử referenceId là đường dẫn đến tài nguyên liên quan
    }
}
