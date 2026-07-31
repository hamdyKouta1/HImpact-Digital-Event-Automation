package com.himpact.dto.event;

import com.himpact.entity.EventStatus;
import com.himpact.entity.EventType;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Detailed event response returned by API endpoints.
 * See: project-index/07_API_Specification.md — Event APIs
 */
public record EventResponse(
        UUID id,
        UUID ownerId,
        String ownerName,
        String title,
        EventType eventType,
        String brideName,
        String groomName,
        String description,
        String venueName,
        String venueAddress,
        String googleMapsUrl,
        LocalDate eventDate,
        LocalTime startTime,
        LocalTime endTime,
        String coverImage,
        UUID packageId,
        String packageName,
        UUID themeId,
        String themeName,
        EventStatus status,
        String slug,
        Instant createdAt,
        Instant updatedAt
) {}
