package com.himpact.service;

import com.himpact.domain.events.RSVPSubmittedEvent;
import com.himpact.dto.rsvp.RsvpResponse;
import com.himpact.dto.rsvp.RsvpStatsResponse;
import com.himpact.dto.rsvp.SubmitRsvpRequest;
import com.himpact.entity.*;
import com.himpact.exception.ResourceNotFoundException;
import com.himpact.repository.EventRepository;
import com.himpact.repository.GuestRepository;
import com.himpact.repository.RsvpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Business logic service for RSVP System.
 * Emits RSVPSubmittedEvent for loose coupling with notifications & dashboard stats.
 *
 * See: project-index/03_Functional_Requirements.md — FR-05 RSVP
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RsvpService {

    private final RsvpRepository rsvpRepository;
    private final GuestRepository guestRepository;
    private final EventRepository eventRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Submit or update RSVP response for a guest.
     */
    @Transactional
    public RsvpResponse submitRsvp(UUID eventId, SubmitRsvpRequest request) {
        Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

        Guest guest = guestRepository.findByInvitationCodeAndIsDeletedFalse(request.invitationCode())
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "code", request.invitationCode()));

        Rsvp rsvp = rsvpRepository.findByGuestId(guest.getId())
                .orElseGet(() -> Rsvp.builder()
                        .guest(guest)
                        .event(event)
                        .build());

        rsvp.setAttendanceStatus(request.attendanceStatus());
        rsvp.setAttendeeCount(request.attendeeCount() != null ? request.attendeeCount() : 1);
        rsvp.setNotes(request.notes());
        rsvp.setResponseTime(Instant.now());

        Rsvp saved = rsvpRepository.save(rsvp);

        // Update guest status
        if (request.attendanceStatus() == AttendanceStatus.ACCEPTED) {
            guest.setStatus(GuestStatus.REGISTERED);
        } else if (request.attendanceStatus() == AttendanceStatus.DECLINED) {
            guest.setStatus(GuestStatus.DECLINED);
        }
        guestRepository.save(guest);

        log.info("RSVP submitted for guest [{}] -> status: [{}]", guest.getFullName(), request.attendanceStatus());

        // Emit domain event asynchronously
        eventPublisher.publishEvent(new RSVPSubmittedEvent(
                saved.getId(), guest.getId(), eventId, saved.getAttendanceStatus().name(), saved.getAttendeeCount()));

        return mapToResponse(saved);
    }

    /**
     * Compute RSVP statistics for event owner dashboard.
     */
    @Transactional(readOnly = true)
    public RsvpStatsResponse getRsvpStats(UUID eventId) {
        long totalInvited = guestRepository.countByEventIdAndIsDeletedFalse(eventId);
        long totalAccepted = rsvpRepository.countByEventIdAndAttendanceStatusAndIsDeletedFalse(eventId, AttendanceStatus.ACCEPTED);
        long totalDeclined = rsvpRepository.countByEventIdAndAttendanceStatusAndIsDeletedFalse(eventId, AttendanceStatus.DECLINED);
        long totalMaybe = rsvpRepository.countByEventIdAndAttendanceStatusAndIsDeletedFalse(eventId, AttendanceStatus.MAYBE);
        long totalPending = totalInvited - (totalAccepted + totalDeclined + totalMaybe);

        List<Rsvp> rsvps = rsvpRepository.findByEventIdAndIsDeletedFalse(eventId);
        long expectedTotalAttendees = rsvps.stream()
                .filter(r -> r.getAttendanceStatus() == AttendanceStatus.ACCEPTED)
                .mapToInt(Rsvp::getAttendeeCount)
                .sum();

        return new RsvpStatsResponse(eventId, totalInvited, totalAccepted, totalDeclined, totalMaybe, Math.max(0, totalPending), expectedTotalAttendees);
    }

    private RsvpResponse mapToResponse(Rsvp rsvp) {
        return new RsvpResponse(
                rsvp.getId(),
                rsvp.getGuest().getId(),
                rsvp.getGuest().getFullName(),
                rsvp.getEvent().getId(),
                rsvp.getAttendanceStatus(),
                rsvp.getAttendeeCount(),
                rsvp.getResponseTime(),
                rsvp.getNotes()
        );
    }
}
