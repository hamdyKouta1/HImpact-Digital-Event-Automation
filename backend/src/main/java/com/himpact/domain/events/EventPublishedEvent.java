package com.himpact.domain.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when an event transitions to PUBLISHED status.
 */
public record EventPublishedEvent(
        UUID eventId,
        UUID ownerId,
        String title,
        String slug,
        Instant timestamp
) {
    public EventPublishedEvent(UUID eventId, UUID ownerId, String title, String slug) {
        this(eventId, ownerId, title, slug, Instant.now());
    }
}
