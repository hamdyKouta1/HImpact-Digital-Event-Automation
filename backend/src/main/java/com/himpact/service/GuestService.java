package com.himpact.service;

import com.himpact.dto.guest.AddGuestRequest;
import com.himpact.dto.guest.GuestResponse;
import com.himpact.dto.guest.ImportGuestsResponse;
import com.himpact.dto.guest.UpdateGuestRequest;
import com.himpact.entity.Event;
import com.himpact.entity.Guest;
import com.himpact.entity.GuestStatus;
import com.himpact.domain.events.GuestAddedEvent;
import com.himpact.exception.ResourceNotFoundException;
import com.himpact.repository.EventRepository;
import com.himpact.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

/**
 * Business logic service for Guest Management.
 * Emits Spring Application Events for loose module coupling.
 *
 * See: project-index/03_Functional_Requirements.md — FR-04 Guest Management
 * See: project-index/05_Software_Architecture.md — Business Layer
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final EventRepository eventRepository;
    private final ApplicationEventPublisher eventPublisher;

    private static final String ALPHANUMERIC = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Add an individual guest to an event.
     */
    @Transactional
    public GuestResponse addGuest(UUID eventId, AddGuestRequest request, UUID createdByUserId) {
        Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

        String invitationCode = generateUniqueInvitationCode();
        String invitationUrl = buildInvitationUrl(event.getSlug(), invitationCode);

        Guest guest = Guest.builder()
                .event(event)
                .fullName(request.fullName())
                .mobile(request.mobile())
                .email(request.email())
                .invitationCode(invitationCode)
                .invitationUrl(invitationUrl)
                .uploadLimit(request.uploadLimit() != null ? request.uploadLimit() : 30)
                .status(GuestStatus.INVITED)
                .createdBy(createdByUserId)
                .build();

        Guest saved = guestRepository.save(guest);
        log.info("Added guest [{}] to event [{}]", saved.getId(), eventId);

        // Emit domain event for loose coupling
        eventPublisher.publishEvent(new GuestAddedEvent(
                saved.getId(), eventId, saved.getFullName(), saved.getInvitationCode()));

        return mapToResponse(saved);
    }

    /**
     * Import guests in batch from a CSV file.
     * Expected CSV format: Name, Mobile, Email
     */
    @Transactional
    public ImportGuestsResponse importGuestsCsv(UUID eventId, InputStream csvInputStream, UUID createdByUserId) {
        Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

        List<String> errors = new ArrayList<>();
        List<String> skipReasons = new ArrayList<>();
        int totalProcessed = 0;
        int successfullyImported = 0;
        int skippedDuplicates = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvInputStream, StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                // Skip header line if present
                if (isHeader && (line.toLowerCase().contains("name") || line.toLowerCase().contains("mobile"))) {
                    isHeader = false;
                    continue;
                }
                isHeader = false;

                totalProcessed++;
                String[] parts = line.split(",");
                if (parts.length < 1 || parts[0].isBlank()) {
                    errors.add("Row " + totalProcessed + ": Name is required");
                    continue;
                }

                String name = parts[0].trim();
                String mobile = parts.length > 1 ? parts[1].trim() : null;
                String email = parts.length > 2 ? parts[2].trim() : null;

                // Duplicate mobile check
                if (mobile != null && !mobile.isBlank() &&
                        guestRepository.findByEventIdAndMobileAndIsDeletedFalse(eventId, mobile).isPresent()) {
                    skippedDuplicates++;
                    skipReasons.add("Row " + totalProcessed + " (" + name + "): Duplicate Mobile (" + mobile + ")");
                    continue;
                }

                // Duplicate email check
                if (email != null && !email.isBlank() &&
                        guestRepository.findByEventIdAndEmailAndIsDeletedFalse(eventId, email).isPresent()) {
                    skippedDuplicates++;
                    skipReasons.add("Row " + totalProcessed + " (" + name + "): Duplicate Email (" + email + ")");
                    continue;
                }

                String invitationCode = generateUniqueInvitationCode();
                String invitationUrl = buildInvitationUrl(event.getSlug(), invitationCode);

                Guest guest = Guest.builder()
                        .event(event)
                        .fullName(name)
                        .mobile(mobile)
                        .email(email)
                        .invitationCode(invitationCode)
                        .invitationUrl(invitationUrl)
                        .status(GuestStatus.INVITED)
                        .createdBy(createdByUserId)
                        .build();

                guestRepository.save(guest);
                successfullyImported++;
            }
        } catch (Exception ex) {
            log.error("CSV guest import failed for event [{}]", eventId, ex);
            errors.add("Import failed: " + ex.getMessage());
        }

        return new ImportGuestsResponse(totalProcessed, successfullyImported, skippedDuplicates, skipReasons, errors);
    }

    /**
     * Get paginated & searchable guest list for an event.
     */
    @Transactional(readOnly = true)
    public Page<GuestResponse> getGuests(UUID eventId, String search, GuestStatus status, Pageable pageable) {
        return guestRepository.searchGuests(eventId, search, status, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Update guest details.
     */
    @Transactional
    public GuestResponse updateGuest(UUID guestId, UpdateGuestRequest request) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest", "id", guestId));

        if (request.fullName() != null) guest.setFullName(request.fullName());
        if (request.mobile() != null) guest.setMobile(request.mobile());
        if (request.email() != null) guest.setEmail(request.email());
        if (request.uploadLimit() != null) guest.setUploadLimit(request.uploadLimit());
        if (request.status() != null) guest.setStatus(request.status());

        Guest updated = guestRepository.save(guest);
        return mapToResponse(updated);
    }

    /**
     * Remove a guest (soft-delete).
     */
    @Transactional
    public void removeGuest(UUID guestId) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest", "id", guestId));

        guest.setDeleted(true);
        guestRepository.save(guest);
        log.info("Soft-deleted guest [{}]", guestId);
    }

    // ── Private Code Generation & Mapping ─────────────────────────────────────

    private String generateUniqueInvitationCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(8);
            for (int i = 0; i < 8; i++) {
                sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
            }
            code = sb.toString();
        } while (guestRepository.existsByInvitationCode(code));
        return code;
    }

    private String buildInvitationUrl(String slug, String code) {
        return "https://himpact.app/invite/" + (slug != null ? slug : "event") + "?code=" + code;
    }

    private GuestResponse mapToResponse(Guest guest) {
        return new GuestResponse(
                guest.getId(),
                guest.getEvent().getId(),
                guest.getFullName(),
                guest.getMobile(),
                guest.getEmail(),
                guest.getInvitationCode(),
                guest.getInvitationUrl(),
                guest.getUploadLimit(),
                guest.getUploadedCount(),
                guest.getStorageUsedMb(),
                guest.getStatus(),
                guest.getCreatedAt()
        );
    }
}
