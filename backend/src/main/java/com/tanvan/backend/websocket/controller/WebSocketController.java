// websocket/controller/WebSocketController.java
package com.tanvan.backend.websocket.controller;

import com.tanvan.backend.notification.dto.response.NotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@lombok.RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send.{projectId}")
    @SendTo("/topic/chat.{projectId}")
    public void sendMessage(@DestinationVariable String projectId, String message) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth != null ? auth.getName() : "anonymous";

        log.info("Chat message in project {}: {} by user {}", projectId, message, userId);
    }

    public void sendNotification(String userId, NotificationResponse notification) {
        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/notifications",
                notification
        );
        log.info("Notification sent to user {}: {}", userId, notification.getId());
    }

    public void broadcastToProject(String projectId, Object payload) {
        messagingTemplate.convertAndSend("/topic/project." + projectId, payload);
    }
}
