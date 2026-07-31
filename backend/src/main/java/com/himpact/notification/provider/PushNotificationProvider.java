package com.himpact.notification.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Web Push / Browser Push implementation of NotificationProvider.
 */
@Slf4j
@Component("pushNotificationProvider")
public class PushNotificationProvider implements NotificationProvider {

    @Override
    public boolean send(NotificationRequest request) {
        log.info("Sending Web Push notification to [{}] with payload: {}", request.recipient(), request.body());
        // Web Push VAPID protocol dispatch logic
        return true;
    }

    @Override
    public String getProviderType() {
        return "PUSH";
    }
}
