package com.himpact.dto.event;

import com.himpact.entity.EventStatus;
import com.himpact.entity.EventType;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Summary event DTO for dashboard list views and cards.
 */
public record EventSummaryResponse(
        UUID id,
        String title,
        EventType eventType,
        LocalDate eventDate,
        String venueName,
        String coverImage,
        EventStatus status,
        String slug,
        long totalGuests,
        long totalUploads
) {}
