package com.himpact.controller;

import com.himpact.dto.PageResponse;
import com.himpact.dto.media.MediaFileResponse;
import com.himpact.dto.media.SyncMediaRequest;
import com.himpact.dto.media.SyncMediaResponse;
import com.himpact.service.MediaService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

/**
 * Media Collection Platform Controller.
 * Base paths:
 *  - /api/v1/events/{eventId}/media
 *  - /api/v1/media/{mediaId}
 *
 * See: project-index/07_API_Specification.md — Media APIs
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Media Platform", description = "Photo and video upload, gallery retrieval, and offline synchronization")
public class MediaController {

    private final MediaService mediaService;

    @Operation(summary = "Upload Media File", description = "Transaction-safe media file upload for event guests.")
    @PostMapping(value = "/events/{eventId}/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadMedia(
            @PathVariable UUID eventId,
            @RequestParam("invitationCode") String invitationCode,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "localIdentifier", required = false) String localIdentifier
    ) {
        MediaFileResponse response = mediaService.uploadMedia(eventId, invitationCode, file, localIdentifier);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Media file uploaded successfully.", response));
    }

    @Operation(summary = "Offline Sync Initialization", description = "Idempotent API for initializing offline media synchronization.")
    @PostMapping("/events/{eventId}/media/sync")
    public ResponseEntity<Map<String, Object>> syncOfflineMedia(
            @PathVariable UUID eventId,
            @Valid @RequestBody SyncMediaRequest request
    ) {
        SyncMediaResponse response = mediaService.syncOfflineMedia(eventId, request);
        return ResponseEntity.ok(ApiResponse.success("Offline sync request processed.", response));
    }

    @Operation(summary = "Get Event Gallery", description = "Paginated list of completed media uploads for an event gallery.")
    @GetMapping("/events/{eventId}/media")
    public ResponseEntity<Map<String, Object>> getEventMedia(
            @PathVariable UUID eventId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("uploadedAt").descending());
        Page<MediaFileResponse> mediaFiles = mediaService.getEventMedia(eventId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Event gallery retrieved.", PageResponse.from(mediaFiles)));
    }

    @Operation(summary = "Delete Media File", description = "Deletes a media file from storage and database.")
    @DeleteMapping("/media/{mediaId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> deleteMedia(
            @PathVariable UUID mediaId
    ) {
        mediaService.deleteMedia(mediaId);
        return ResponseEntity.ok(ApiResponse.success("Media file deleted successfully."));
    }
}
