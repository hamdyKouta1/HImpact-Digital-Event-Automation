package com.himpact.notification.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Provider-agnostic stub for WhatsApp Business API.
 * Pluggable implementation for future releases without modifying business logic.
 */
@Slf4j
@Component("whatsappNotificationProvider")
public class WhatsAppNotificationProvider implements NotificationProvider {

    @Override
    public boolean send(NotificationRequest request) {
        log.info("[WhatsApp Stub] Notification queued for recipient [{}]", request.recipient());
        return true;
    }

    @Override
    public String getProviderType() {
        return "WHATSAPP";
    }
}
