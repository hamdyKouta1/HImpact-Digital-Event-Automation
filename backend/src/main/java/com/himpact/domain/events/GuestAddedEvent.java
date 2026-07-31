package com.himpact.domain.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a guest is added to an event.
 */
public record GuestAddedEvent(
        UUID guestId,
        UUID eventId,
        String guestName,
        String invitationCode,
        Instant timestamp
) {
    public GuestAddedEvent(UUID guestId, UUID eventId, String guestName, String invitationCode) {
        this(guestId, eventId, guestName, invitationCode, Instant.now());
    }
}
