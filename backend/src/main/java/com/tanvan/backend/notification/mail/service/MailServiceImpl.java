package com.tanvan.backend.notification.mail.service;

import java.util.Locale;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.mail.from-name}")
    private String fromName;

    @Value("${app.mail.enabled:true}")
    private boolean enabled;

    @Override
    @Async("mailExecutor")   // gửi bất đồng bộ, không block request
    public void sendHtml(String to, String subject, String templateName, Map<String, Object> vars) {
        if (!enabled) {
            log.debug("Mail disabled, skip sending to {}", to);
            return;
        }
        try {
            Context ctx = new Context(Locale.getDefault(), vars);
            String html = templateEngine.process("mail/" + templateName, ctx);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress(from, fromName));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Mail sent to {} - {}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send mail to {}: {}", to, e.getMessage(), e);
            // Không throw để tránh làm hỏng business flow
        }
    }
}
