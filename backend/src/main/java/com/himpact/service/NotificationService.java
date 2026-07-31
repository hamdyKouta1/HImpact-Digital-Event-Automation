package com.himpact.service;

import com.himpact.dto.PageResponse;
import com.himpact.entity.Notification;
import com.himpact.notification.provider.NotificationProvider;
import com.himpact.notification.provider.NotificationRequest;
import com.himpact.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Notification Orchestration Service.
 * Does NOT execute email or push directly.
 * Instead, delegates to NotificationProvider implementations (Email, Push, WhatsApp, SMS).
 *
 * See: PO Sprint 5 Workstream A Architecture Requirement
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final Map<String, NotificationProvider> notificationProviders;

    /**
     * Dispatch notification via appropriate provider channel.
     */
    @Transactional
    public Notification dispatchNotification(String channel, String recipient, String subject, String body) {
        log.info("Orchestrating notification dispatch for channel [{}] to [{}]", channel, recipient);

        Notification notification = Notification.builder()
                .recipient(recipient)
                .deliveryChannel(channel)
                .subject(subject)
                .content(body)
                .status("PENDING")
                .build();

        Notification saved = notificationRepository.save(notification);

        // Find provider bean for channel
        NotificationProvider provider = findProvider(channel);
        if (provider != null) {
            boolean success = provider.send(new NotificationRequest(recipient, subject, body));
            if (success) {
                saved.setStatus("SENT");
                saved.setSentAt(Instant.now());
            } else {
                saved.setStatus("FAILED");
                saved.setErrorMessage("Provider delivery failed");
                saved.setRetryCount(saved.getRetryCount() + 1);
            }
        } else {
            log.warn("No NotificationProvider registered for channel [{}]", channel);
            saved.setStatus("FAILED");
            saved.setErrorMessage("No provider available for channel: " + channel);
        }

        return notificationRepository.save(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<Notification> getEventNotifications(UUID eventId, Pageable pageable) {
        return PageResponse.from(notificationRepository.findByEventIdAndIsDeletedFalse(eventId, pageable));
    }

    private NotificationProvider findProvider(String channel) {
        for (NotificationProvider provider : notificationProviders.values()) {
            if (provider.getProviderType().equalsIgnoreCase(channel)) {
                return provider;
            }
        }
        return null;
    }
}
