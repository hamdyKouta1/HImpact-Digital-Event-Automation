package com.himpact.controller;

import com.himpact.dto.event.CreateEventRequest;
import com.himpact.dto.event.EventResponse;
import com.himpact.dto.event.EventSummaryResponse;
import com.himpact.dto.event.UpdateEventRequest;
import com.himpact.security.HimpactUserPrincipal;
import com.himpact.service.EventService;
import com.himpact.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Event Management Controller.
 * Method-level security is enforced via @PreAuthorize expressions backed by EventSecurityEvaluator.
 *
 * Base path: /api/v1/events
 * See: project-index/07_API_Specification.md — Event APIs
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Event Management", description = "Create, configure, publish, and manage digital events")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Create Event", description = "Creates a new event in DRAFT status for the authenticated user.")
    @PostMapping("/events")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> createEvent(
            @AuthenticationPrincipal HimpactUserPrincipal principal,
            @Valid @RequestBody CreateEventRequest request
    ) {
        EventResponse event = eventService.createEvent(principal.userId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Event created successfully.", event));
    }

    @Operation(summary = "List My Events", description = "Returns all active events owned by the authenticated user.")
    @GetMapping("/events")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getMyEvents(
            @AuthenticationPrincipal HimpactUserPrincipal principal
    ) {
        List<EventSummaryResponse> events = eventService.getMyEvents(principal.userId());
        return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully.", events));
    }

    @Operation(summary = "Get Event Details", description = "Fetches detailed information for a specific event.")
    @GetMapping("/events/{eventId}")
    @PreAuthorize("@eventSecurity.isGuestOrOwner(#eventId)")
    public ResponseEntity<Map<String, Object>> getEvent(
            @PathVariable UUID eventId
    ) {
        EventResponse event = eventService.getEvent(eventId);
        return ResponseEntity.ok(ApiResponse.success("Event details retrieved.", event));
    }

    @Operation(summary = "Update Event", description = "Updates metadata and settings for an event.")
    @PutMapping("/events/{eventId}")
    @PreAuthorize("@eventSecurity.isOwner(#eventId)")
    public ResponseEntity<Map<String, Object>> updateEvent(
            @PathVariable UUID eventId,
            @Valid @RequestBody UpdateEventRequest request
    ) {
        EventResponse event = eventService.updateEvent(eventId, request);
        return ResponseEntity.ok(ApiResponse.success("Event updated successfully.", event));
    }

    @Operation(summary = "Publish Event", description = "Publishes a draft event, making it active for guests.")
    @PostMapping("/events/{eventId}/publish")
    @PreAuthorize("@eventSecurity.isOwner(#eventId)")
    public ResponseEntity<Map<String, Object>> publishEvent(
            @PathVariable UUID eventId
    ) {
        EventResponse event = eventService.publishEvent(eventId);
        return ResponseEntity.ok(ApiResponse.success("Event published successfully.", event));
    }

    @Operation(summary = "Delete Event", description = "Soft-deletes a draft event.")
    @DeleteMapping("/events/{eventId}")
    @PreAuthorize("@eventSecurity.isOwner(#eventId)")
    public ResponseEntity<Map<String, Object>> deleteEvent(
            @PathVariable UUID eventId
    ) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok(ApiResponse.success("Event deleted successfully."));
    }
}
