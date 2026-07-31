package com.himpact.service;

import com.himpact.domain.events.MediaUploadedEvent;
import com.himpact.dto.media.MediaFileResponse;
import com.himpact.dto.media.SyncMediaRequest;
import com.himpact.dto.media.SyncMediaResponse;
import com.himpact.entity.*;
import com.himpact.exception.BusinessRuleException;
import com.himpact.exception.ResourceNotFoundException;
import com.himpact.repository.*;
import com.himpact.storage.StorageProvider;
import com.himpact.storage.UploadResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Business logic service for Media Platform.
 * Enforces transaction-safe uploads, storage provider integration, quota checks, and rollback file cleanup.
 *
 * See: project-index/03_Functional_Requirements.md — FR-08 Media Upload, FR-09 Storage
 * See: project-index/05_Software_Architecture.md — Storage & Media Pipeline
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaFileRepository mediaFileRepository;
    private final MediaSyncRepository mediaSyncRepository;
    private final EventRepository eventRepository;
    private final GuestRepository guestRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Qualifier("localStorageProvider")
    private final StorageProvider storageProvider;

    /**
     * Upload a photo or video for an event.
     * Transaction-safe: cleans up storage file if database save fails.
     */
    @Transactional
    public MediaFileResponse uploadMedia(
            UUID eventId,
            String invitationCode,
            MultipartFile file,
            String localIdentifier
    ) {
        // 1. Verify Event exists & is published
        Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

        if (event.getStatus() != EventStatus.PUBLISHED && event.getStatus() != EventStatus.DRAFT) {
            throw new BusinessRuleException("Event is not accepting media uploads.");
        }

        // 1b. Server-Side MIME Validation & Size Limits (PO Condition 2)
        String mimeType = file.getContentType() != null ? file.getContentType().toLowerCase() : "";
        long fileSize = file.getSize();

        boolean isImage = mimeType.equals("image/jpeg") || mimeType.equals("image/jpg")
                || mimeType.equals("image/png") || mimeType.equals("image/webp") || mimeType.equals("image/heic");
        boolean isVideo = mimeType.equals("video/mp4") || mimeType.equals("video/quicktime");

        if (!isImage && !isVideo) {
            throw new BusinessRuleException("Invalid media type [" + mimeType + "]. Only JPG, PNG, WEBP, HEIC images and MP4, MOV videos are allowed.");
        }

        long maxBytes = isImage ? 20L * 1024 * 1024 : 200L * 1024 * 1024; // 20MB for images, 200MB for videos
        if (fileSize > maxBytes) {
            long maxMb = isImage ? 20 : 200;
            throw new BusinessRuleException("File size exceeds limit (" + maxMb + " MB maximum for " + (isImage ? "photos" : "videos") + ").");
        }

        // 2. Verify Guest by code & check quota
        Guest guest = guestRepository.findByInvitationCodeAndIsDeletedFalse(invitationCode)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "code", invitationCode));

        if (!guest.getEvent().getId().equals(event.getId())) {
            throw new BusinessRuleException("Invitation code does not match this event.");
        }

        if (guest.getUploadedCount() >= guest.getUploadLimit()) {
            throw new BusinessRuleException("Upload quota exceeded. Limit is " + guest.getUploadLimit() + " files.");
        }

        // 3. Store file via StorageProvider
        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "photo.jpg";
        String folderPath = "events/" + event.getSlug() + "/guests/" + guest.getInvitationCode();

        UploadResult uploadResult;
        try {
            uploadResult = storageProvider.upload(
                    folderPath,
                    originalFilename,
                    file.getBytes(),
                    file.getContentType() != null ? file.getContentType() : "image/jpeg"
            );
        } catch (Exception ex) {
            log.error("Storage provider upload failed for event [{}]", eventId, ex);
            throw new BusinessRuleException("Storage provider error: " + ex.getMessage());
        }

        if (!uploadResult.success()) {
            throw new BusinessRuleException("Failed to upload file: " + uploadResult.errorMessage());
        }

        // 4. Save MediaFile & MediaSync in DB (with rollback file cleanup protection)
        MediaFile mediaFile;
        try {
            mediaFile = MediaFile.builder()
                    .event(event)
                    .guest(guest)
                    .originalFilename(originalFilename)
                    .storageFilename(uploadResult.storageFilename())
                    .mimeType(uploadResult.mimeType())
                    .fileSize(uploadResult.fileSize())
                    .storageProvider(storageProvider.getProviderName())
                    .storagePath(uploadResult.storagePath())
                    .uploadStatus(UploadStatus.COMPLETED)
                    .uploadedAt(Instant.now())
                    .build();

            mediaFile = mediaFileRepository.save(mediaFile);

            // Increment guest upload counters
            guest.setUploadedCount(guest.getUploadedCount() + 1);
            BigDecimal addedMb = BigDecimal.valueOf(file.getSize())
                    .divide(BigDecimal.valueOf(1024 * 1024), 2, RoundingMode.HALF_UP);
            guest.setStorageUsedMb(guest.getStorageUsedMb().add(addedMb));
            guestRepository.save(guest);

            // Record MediaSync entry for offline deduplication if identifier provided
            if (localIdentifier != null && !localIdentifier.isBlank()) {
                MediaSync sync = MediaSync.builder()
                        .event(event)
                        .guest(guest)
                        .mediaFile(mediaFile)
                        .localIdentifier(localIdentifier)
                        .syncStatus(SyncStatus.COMPLETED)
                        .synchronizedAt(Instant.now())
                        .build();
                mediaSyncRepository.save(sync);
            }

        } catch (Exception dbEx) {
            // DB Save Failed — Clean up orphaned file from storage provider (Transaction Rollback Cleanup)
            log.error("DB save failed during media upload. Cleaning up file at: {}", uploadResult.storagePath(), dbEx);
            storageProvider.delete(uploadResult.storagePath());
            throw dbEx;
        }

        log.info("Media file uploaded successfully [{}] for event [{}]", mediaFile.getId(), eventId);

        // 5. Emit domain event (dispatched AFTER_COMMIT by listeners)
        eventPublisher.publishEvent(new MediaUploadedEvent(
                mediaFile.getId(), eventId, guest.getId(), originalFilename, file.getSize()));

        return mapToResponse(mediaFile);
    }

    /**
     * Idempotent Offline Sync Initialization API.
     * Prevents duplicate uploads when a client reconnects and syncs its offline queue.
     */
    @Transactional
    public SyncMediaResponse syncOfflineMedia(UUID eventId, SyncMediaRequest request) {
        Guest guest = guestRepository.findByInvitationCodeAndIsDeletedFalse(request.invitationCode())
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "code", request.invitationCode()));

        // Idempotency check: if local_identifier already synced, return existing state
        Optional<MediaSync> existingSync = mediaSyncRepository.findByGuestIdAndLocalIdentifier(
                guest.getId(), request.localIdentifier());

        if (existingSync.isPresent() && existingSync.get().getSyncStatus() == SyncStatus.COMPLETED) {
            log.info("Offline sync request already processed for local_identifier [{}]", request.localIdentifier());
            MediaFile file = existingSync.get().getMediaFile();
            return new SyncMediaResponse(
                    existingSync.get().getId(),
                    request.localIdentifier(),
                    "COMPLETED",
                    true,
                    file != null ? mapToResponse(file) : null
            );
        }

        // Initialize new MediaSync tracking record
        Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

        MediaSync sync = MediaSync.builder()
                .event(event)
                .guest(guest)
                .localIdentifier(request.localIdentifier())
                .syncStatus(SyncStatus.PENDING)
                .build();

        MediaSync saved = mediaSyncRepository.save(sync);
        return new SyncMediaResponse(saved.getId(), request.localIdentifier(), "PENDING", false, null);
    }

    /**
     * Paginated gallery retrieval for event guests & owner.
     */
    @Transactional(readOnly = true)
    public Page<MediaFileResponse> getEventMedia(UUID eventId, Pageable pageable) {
        return mediaFileRepository.findByEventIdAndUploadStatusAndIsDeletedFalse(eventId, UploadStatus.COMPLETED, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Delete media file.
     */
    @Transactional
    public void deleteMedia(UUID mediaId) {
        MediaFile mediaFile = mediaFileRepository.findByIdAndIsDeletedFalse(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("MediaFile", "id", mediaId));

        storageProvider.delete(mediaFile.getStoragePath());
        mediaFile.setUploadStatus(UploadStatus.DELETED);
        mediaFile.setDeleted(true);
        mediaFileRepository.save(mediaFile);
    }

    private MediaFileResponse mapToResponse(MediaFile file) {
        return new MediaFileResponse(
                file.getId(),
                file.getEvent().getId(),
                file.getGuest().getId(),
                file.getGuest().getFullName(),
                file.getOriginalFilename(),
                file.getMimeType(),
                file.getFileSize(),
                file.getStorageProvider(),
                file.getStoragePath(),
                file.getUploadStatus(),
                file.getUploadedAt()
        );
    }
}
