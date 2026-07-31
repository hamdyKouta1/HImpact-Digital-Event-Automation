package com.himpact.dto.event;

import com.himpact.entity.EventType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Request body for creating an event.
 * See: project-index/07_API_Specification.md — POST /events
 */
public record CreateEventRequest(
        @NotBlank(message = "Event title is required")
        @Size(max = 255, message = "Title cannot exceed 255 characters")
        String title,

        @NotNull(message = "Event type is required")
        EventType eventType,

        String brideName,
        String groomName,

        @Size(max = 2000, message = "Description cannot exceed 2000 characters")
        String description,

        @NotBlank(message = "Venue name is required")
        String venueName,

        String venueAddress,
        String googleMapsUrl,

        @NotNull(message = "Event date is required")
        @FutureOrPresent(message = "Event date must be in the present or future")
        LocalDate eventDate,

        LocalTime startTime,
        LocalTime endTime,

        String coverImage,
        UUID packageId,
        UUID themeId
) {}
