package com.himpact.controller;

import com.himpact.dto.PageResponse;
import com.himpact.dto.guest.AddGuestRequest;
import com.himpact.dto.guest.GuestResponse;
import com.himpact.dto.guest.ImportGuestsResponse;
import com.himpact.dto.guest.UpdateGuestRequest;
import com.himpact.entity.GuestStatus;
import com.himpact.security.HimpactUserPrincipal;
import com.himpact.service.GuestService;
import com.himpact.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Guest Management Controller.
 * Method-level security is enforced via @PreAuthorize expressions.
 *
 * Base paths:
 *  - /api/v1/events/{eventId}/guests
 *  - /api/v1/guests/{guestId}
 *
 * See: project-index/07_API_Specification.md — Guest APIs
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Guest Management", description = "Add, import, search, and manage invited guests")
public class GuestController {

    private final GuestService guestService;

    @Operation(summary = "Add Guest", description = "Adds an individual guest to an event.")
    @PostMapping("/events/{eventId}/guests")
    @PreAuthorize("@eventSecurity.isOwner(#eventId)")
    public ResponseEntity<Map<String, Object>> addGuest(
            @PathVariable UUID eventId,
            @AuthenticationPrincipal HimpactUserPrincipal principal,
            @Valid @RequestBody AddGuestRequest request
    ) {
        GuestResponse guest = guestService.addGuest(eventId, request, principal.userId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Guest added successfully.", guest));
    }

    @Operation(summary = "Import Guests CSV", description = "Batch imports guests from a CSV file (Name, Mobile, Email).")
    @PostMapping(value = "/events/{eventId}/guests/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@eventSecurity.isOwner(#eventId)")
    public ResponseEntity<Map<String, Object>> importGuests(
            @PathVariable UUID eventId,
            @AuthenticationPrincipal HimpactUserPrincipal principal,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.success("Uploaded CSV file is empty."));
        }

        ImportGuestsResponse result = guestService.importGuestsCsv(eventId, file.getInputStream(), principal.userId());
        return ResponseEntity.ok(ApiResponse.success("Guest import completed.", result));
    }

    @Operation(summary = "List / Search Guests", description = "Returns a paginated list of guests with optional search filter.")
    @GetMapping("/events/{eventId}/guests")
    @PreAuthorize("@eventSecurity.isOwner(#eventId)")
    public ResponseEntity<Map<String, Object>> getGuests(
            @PathVariable UUID eventId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) GuestStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<GuestResponse> guests = guestService.getGuests(eventId, search, status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Guests retrieved successfully.", PageResponse.from(guests)));
    }

    @Operation(summary = "Update Guest", description = "Updates details or status for a guest.")
    @PutMapping("/guests/{guestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> updateGuest(
            @PathVariable UUID guestId,
            @Valid @RequestBody UpdateGuestRequest request
    ) {
        GuestResponse guest = guestService.updateGuest(guestId, request);
        return ResponseEntity.ok(ApiResponse.success("Guest updated successfully.", guest));
    }

    @Operation(summary = "Remove Guest", description = "Soft-deletes a guest record.")
    @DeleteMapping("/guests/{guestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> removeGuest(
            @PathVariable UUID guestId
    ) {
        guestService.removeGuest(guestId);
        return ResponseEntity.ok(ApiResponse.success("Guest removed successfully."));
    }
}
