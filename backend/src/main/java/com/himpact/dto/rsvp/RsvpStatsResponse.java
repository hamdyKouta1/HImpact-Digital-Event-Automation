package com.himpact.dto.rsvp;

import java.util.UUID;

/**
 * Summary RSVP stats for event owner dashboard.
 */
public record RsvpStatsResponse(
        UUID eventId,
        long totalInvited,
        long totalAccepted,
        long totalDeclined,
        long totalMaybe,
        long totalPending,
        long expectedTotalAttendees
) {}
