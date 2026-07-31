package com.himpact.domain.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a new event is created.
 */
public record EventCreatedEvent(
        UUID eventId,
        UUID ownerId,
        String title,
        String eventType,
        Instant timestamp
) {
    public EventCreatedEvent(UUID eventId, UUID ownerId, String title, String eventType) {
        this(eventId, ownerId, title, eventType, Instant.now());
    }
}
