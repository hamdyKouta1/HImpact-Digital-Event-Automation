package com.himpact.notification.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Provider-agnostic stub for SMS / OTP providers (Firebase Phone Auth / Twilio).
 */
@Slf4j
@Component("smsNotificationProvider")
public class SmsNotificationProvider implements NotificationProvider {

    @Override
    public boolean send(NotificationRequest request) {
        log.info("[SMS Stub] Notification queued for recipient [{}]", request.recipient());
        return true;
    }

    @Override
    public String getProviderType() {
        return "SMS";
    }
}
