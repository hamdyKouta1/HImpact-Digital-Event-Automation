package com.himpact.domain.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a media file is uploaded by a guest.
 */
public record MediaUploadedEvent(
        UUID mediaFileId,
        UUID eventId,
        UUID guestId,
        String originalFilename,
        long fileSize,
        Instant timestamp
) {
    public MediaUploadedEvent(UUID mediaFileId, UUID eventId, UUID guestId, String originalFilename, long fileSize) {
        this(mediaFileId, eventId, guestId, originalFilename, fileSize, Instant.now());
    }
}
