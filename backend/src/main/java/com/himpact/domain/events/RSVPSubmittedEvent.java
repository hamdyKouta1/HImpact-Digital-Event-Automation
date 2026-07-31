package com.himpact.domain.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a guest submits or updates their RSVP response.
 */
public record RSVPSubmittedEvent(
        UUID rsvpId,
        UUID guestId,
        UUID eventId,
        String attendanceStatus,
        int attendeeCount,
        Instant timestamp
) {
    public RSVPSubmittedEvent(UUID rsvpId, UUID guestId, UUID eventId, String attendanceStatus, int attendeeCount) {
        this(rsvpId, guestId, eventId, attendanceStatus, attendeeCount, Instant.now());
    }
}
