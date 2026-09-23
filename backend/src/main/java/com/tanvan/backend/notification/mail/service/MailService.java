package com.tanvan.backend.notification.mail.service;

import java.util.Map;

public interface MailService {
    void sendHtml(String to, String subject, String templateName, Map<String, Object> variables);
    // void sendRaw(String to, String subject, String content);
    // void sendAsync(String to, String subject, String templateName, Map<String, Object> variables);
}