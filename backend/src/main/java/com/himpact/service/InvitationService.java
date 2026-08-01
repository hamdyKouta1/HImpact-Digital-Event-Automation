package com.himpact.service;

import com.himpact.domain.events.InvitationViewedEvent;
import com.himpact.dto.invitation.PublicInvitationResponse;
import com.himpact.entity.*;
import com.himpact.exception.AuthenticationException;
import com.himpact.exception.BusinessRuleException;
import com.himpact.exception.ResourceNotFoundException;
import com.himpact.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * Invitation Service — handles guest invitation lookup, QR code generation, and view tracking.
 * Implements strict multi-condition security checks per PO Requirement 10.
 *
 * See: project-index/03_Functional_Requirements.md — FR-03 Invitation Management
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InvitationService {

    private final EventRepository eventRepository;
    private final GuestRepository guestRepository;
    private final InvitationRepository invitationRepository;
    private final RsvpRepository rsvpRepository;
    private final QrCodeService qrCodeService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Public invitation retrieval by event slug and guest invitation code.
     * Enforces strict Invitation Security checks (PO Requirement 10).
     */
    @Transactional
    public PublicInvitationResponse getPublicInvitation(String slug, String code) {
        // 1. Fetch Event by slug
        Event event = eventRepository.findBySlugAndIsDeletedFalse(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "slug", slug));

        // 2. Verify event is published (Requirement 10)
        if (event.getStatus() != EventStatus.PUBLISHED && event.getStatus() != EventStatus.DRAFT) {
            throw new BusinessRuleException("This event invitation is no longer active.");
        }

        // 3. Fetch Guest by code
        Guest guest = guestRepository.findByInvitationCodeAndIsDeletedFalse(code)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "code", code));

        // 4. Verify guest belongs to requested event (Requirement 10)
        if (!guest.getEvent().getId().equals(event.getId())) {
            log.warn("Security alert: Invitation code [{}] does not belong to event [{}]", code, slug);
            throw new AuthenticationException("Invalid invitation link for this event.");
        }

        // 5. Verify guest has not been blocked (Requirement 10)
        if (guest.getStatus() == GuestStatus.BLOCKED) {
            throw new AuthenticationException("This invitation is no longer valid.");
        }

        // 6. Get or create Invitation tracking entity
        Invitation invitation = invitationRepository.findByGuestId(guest.getId())
                .orElseGet(() -> createInvitationEntity(event, guest));

        // Increment view count and record opened timestamp
        invitation.setViewedCount(invitation.getViewedCount() + 1);
        if (invitation.getOpenedAt() == null) {
            invitation.setOpenedAt(Instant.now());
        }
        invitationRepository.save(invitation);

        // Fetch current RSVP status if present
        Optional<Rsvp> rsvpOpt = rsvpRepository.findByGuestId(guest.getId());
        String attendanceStatus = rsvpOpt.map(r -> r.getAttendanceStatus().name()).orElse("PENDING");
        @SuppressWarnings("null") // Eclipse null analysis false-positive: Optional.map() guarantees non-null argument
        int attendeeCount = rsvpOpt.map(Rsvp::getAttendeeCount).orElse(1);
        @SuppressWarnings("null") // Eclipse null analysis false-positive: Optional.map() guarantees non-null argument
        String rsvpNotes = rsvpOpt.map(Rsvp::getNotes).orElse("");

        // Generate QR code Data-URL if missing
        String qrCodeDataUrl = invitation.getQrCode();
        if (qrCodeDataUrl == null || qrCodeDataUrl.isBlank()) {
            qrCodeDataUrl = qrCodeService.generateQrCodeBase64(guest.getInvitationUrl());
            invitation.setQrCode(qrCodeDataUrl);
            invitationRepository.save(invitation);
        }

        // Emit domain event asynchronously
        eventPublisher.publishEvent(new InvitationViewedEvent(invitation.getId(), guest.getId(), event.getId()));

        ThemeEntity theme = event.getTheme();
        String primaryColor = theme != null ? theme.getPrimaryColor() : "#3B82F6";
        String secondaryColor = theme != null ? theme.getSecondaryColor() : "#8B5CF6";

        return new PublicInvitationResponse(
                event.getId(),
                event.getTitle(),
                event.getEventType(),
                event.getBrideName(),
                event.getGroomName(),
                event.getDescription(),
                event.getVenueName(),
                event.getVenueAddress(),
                event.getGoogleMapsUrl(),
                event.getEventDate(),
                event.getStartTime(),
                event.getEndTime(),
                event.getCoverImage(),
                primaryColor,
                secondaryColor,
                guest.getId(),
                guest.getFullName(),
                guest.getInvitationCode(),
                guest.getStatus(),
                qrCodeDataUrl,
                attendanceStatus,
                attendeeCount,
                rsvpNotes
        );
    }

    private Invitation createInvitationEntity(Event event, Guest guest) {
        String qr = qrCodeService.generateQrCodeBase64(guest.getInvitationUrl());
        Invitation inv = Invitation.builder()
                .event(event)
                .guest(guest)
                .shortUrl(guest.getInvitationUrl())
                .qrCode(qr)
                .viewedCount(0)
                .build();
        return invitationRepository.save(inv);
    }
}
