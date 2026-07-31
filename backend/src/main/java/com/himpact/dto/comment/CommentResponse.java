package com.himpact.dto.comment;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID eventId,
        UUID guestId,
        String guestName,
        String message,
        Instant createdAt
) {}
