package com.himpact.domain.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a payment transitions to APPROVED state.
 * Triggers package activation via PackageActivationListener without tight service coupling.
 *
 * See: PO Sprint 5 Workstream B Payment Architecture
 */
public record PaymentApprovedEvent(
        UUID paymentId,
        UUID eventId,
        UUID packageId,
        UUID approvedByAdminId,
        Instant timestamp
) {
    public PaymentApprovedEvent(UUID paymentId, UUID eventId, UUID packageId, UUID approvedByAdminId) {
        this(paymentId, eventId, packageId, approvedByAdminId, Instant.now());
    }
}
