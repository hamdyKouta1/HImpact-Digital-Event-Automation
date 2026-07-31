package com.himpact.dto.invitation;

import com.himpact.entity.EventType;
import com.himpact.entity.GuestStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Public invitation response returned when a guest views their invitation link.
 * Contains event details, couple info, venue details, guest info, QR code, and current RSVP status.
 */
public record PublicInvitationResponse(
        UUID eventId,
        String title,
        EventType eventType,
        String brideName,
        String groomName,
        String description,
        String venueName,
        String venueAddress,
        String googleMapsUrl,
        LocalDate eventDate,
        LocalTime startTime,
        LocalTime endTime,
        String coverImage,
        String primaryColor,
        String secondaryColor,
        // Guest info
        UUID guestId,
        String guestName,
        String invitationCode,
        GuestStatus guestStatus,
        String qrCodeDataUrl,
        // Current RSVP
        String attendanceStatus,
        int attendeeCount,
        String rsvpNotes
) {}
