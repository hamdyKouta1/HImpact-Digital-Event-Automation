package com.himpact.notification.provider;

/**
 * Common Notification Provider Interface.
 * Every delivery channel provider (Email, Push, WhatsApp, SMS) must implement this interface.
 * NotificationService orchestrates providers without direct coupling to delivery mechanisms.
 *
 * See: PO Sprint 5 Workstream A Architecture Requirement
 */
public interface NotificationProvider {

    /**
     * Dispatch a notification to the target recipient.
     */
    boolean send(NotificationRequest request);

    /**
     * Unique identifier for the provider channel (EMAIL, PUSH, WHATSAPP, SMS).
     */
    String getProviderType();
}
