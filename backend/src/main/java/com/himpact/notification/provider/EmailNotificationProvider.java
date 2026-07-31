package com.himpact.notification.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Email implementation of NotificationProvider using Spring JavaMailSender.
 */
@Slf4j
@Component("emailNotificationProvider")
@RequiredArgsConstructor
public class EmailNotificationProvider implements NotificationProvider {

    private final JavaMailSender mailSender;

    @Override
    public boolean send(NotificationRequest request) {
        try {
            log.info("Sending Email notification to [{}] with subject [{}]", request.recipient(), request.subject());

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(request.recipient());
            mailMessage.setSubject(request.subject());
            mailMessage.setText(request.body());
            mailMessage.setFrom("noreply@himpact.app");

            mailSender.send(mailMessage);
            log.info("Email notification successfully sent to [{}]", request.recipient());
            return true;
        } catch (Exception ex) {
            log.error("Failed to send Email notification to [{}]", request.recipient(), ex);
            return false;
        }
    }

    @Override
    public String getProviderType() {
        return "EMAIL";
    }
}
