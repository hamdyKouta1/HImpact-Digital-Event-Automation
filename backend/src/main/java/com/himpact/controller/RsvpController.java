package com.himpact.controller;

import com.himpact.dto.rsvp.RsvpResponse;
import com.himpact.dto.rsvp.RsvpStatsResponse;
import com.himpact.dto.rsvp.SubmitRsvpRequest;
import com.himpact.service.RsvpService;
import com.himpact.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * RSVP Controller.
 * Base path: /api/v1/events/{eventId}/rsvp
 * See: project-index/07_API_Specification.md — RSVP APIs
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/events/{eventId}/rsvp")
@RequiredArgsConstructor
@Tag(name = "RSVP System", description = "Submit guest attendance responses and view event RSVP statistics")
public class RsvpController {

    private final RsvpService rsvpService;

    @Operation(summary = "Submit RSVP", description = "Public guest endpoint for submitting or updating attendance response.")
    @PostMapping
    public ResponseEntity<Map<String, Object>> submitRsvp(
            @PathVariable UUID eventId,
            @Valid @RequestBody SubmitRsvpRequest request
    ) {
        RsvpResponse response = rsvpService.submitRsvp(eventId, request);
        return ResponseEntity.ok(ApiResponse.success("RSVP response recorded.", response));
    }

    @Operation(summary = "RSVP Statistics", description = "Owner endpoint for viewing aggregate RSVP stats.")
    @GetMapping("/stats")
    @PreAuthorize("@eventSecurity.isOwner(#eventId)")
    public ResponseEntity<Map<String, Object>> getRsvpStats(
            @PathVariable UUID eventId
    ) {
        RsvpStatsResponse stats = rsvpService.getRsvpStats(eventId);
        return ResponseEntity.ok(ApiResponse.success("RSVP statistics retrieved.", stats));
    }
}
