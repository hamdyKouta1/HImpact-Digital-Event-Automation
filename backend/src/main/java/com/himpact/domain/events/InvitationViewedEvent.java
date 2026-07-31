package com.himpact.domain.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a guest opens their digital invitation page.
 */
public record InvitationViewedEvent(
        UUID invitationId,
        UUID guestId,
        UUID eventId,
        Instant timestamp
) {
    public InvitationViewedEvent(UUID invitationId, UUID guestId, UUID eventId) {
        this(invitationId, guestId, eventId, Instant.now());
    }
}
