package com.himpact.dto.rsvp;

import com.himpact.entity.AttendanceStatus;

import java.time.Instant;
import java.util.UUID;

public record RsvpResponse(
        UUID id,
        UUID guestId,
        String guestName,
        UUID eventId,
        AttendanceStatus attendanceStatus,
        int attendeeCount,
        Instant responseTime,
        String notes
) {}
