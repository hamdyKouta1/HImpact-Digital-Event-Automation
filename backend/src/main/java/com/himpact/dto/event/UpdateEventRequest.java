package com.himpact.dto.event;

import com.himpact.entity.EventType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Request body for updating an event.
 * See: project-index/07_API_Specification.md — PUT /events/{eventId}
 */
public record UpdateEventRequest(
        @Size(max = 255)
        String title,

        EventType eventType,
        String brideName,
        String groomName,

        @Size(max = 2000)
        String description,

        String venueName,
        String venueAddress,
        String googleMapsUrl,

        @FutureOrPresent
        LocalDate eventDate,

        LocalTime startTime,
        LocalTime endTime,

        String coverImage,
        UUID themeId
) {}
