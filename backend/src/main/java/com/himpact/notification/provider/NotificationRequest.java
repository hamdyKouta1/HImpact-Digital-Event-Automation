package com.himpact.notification.provider;

import java.util.Map;

/**
 * Common request wrapper for notification dispatch across all providers.
 */
public record NotificationRequest(
        String recipient,
        String subject,
        String body,
        String templateName,
        Map<String, Object> templateModel
) {
    public NotificationRequest(String recipient, String subject, String body) {
        this(recipient, subject, body, null, Map.of());
    }
}
