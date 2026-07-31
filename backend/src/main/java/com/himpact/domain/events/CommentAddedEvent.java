package com.himpact.domain.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a congratulatory wish is posted on the event wall.
 */
public record CommentAddedEvent(
        UUID commentId,
        UUID eventId,
        UUID guestId,
        String messageSnippet,
        Instant timestamp
) {
    public CommentAddedEvent(UUID commentId, UUID eventId, UUID guestId, String messageSnippet) {
        this(commentId, eventId, guestId, messageSnippet, Instant.now());
    }
}
