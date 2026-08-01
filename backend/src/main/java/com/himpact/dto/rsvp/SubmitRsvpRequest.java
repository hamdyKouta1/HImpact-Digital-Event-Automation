package com.himpact.dto.rsvp;

import com.himpact.entity.AttendanceStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request body for submitting or updating RSVP response.
 * See: project-index/07_API_Specification.md — RSVP APIs
 */
public record SubmitRsvpRequest(
        @NotBlank(message = "Invitation code is required")
        String invitationCode,

        @NotNull(message = "Attendance status is required")
        AttendanceStatus attendanceStatus,

        @Min(value = 1, message = "Attendee count must be at least 1")
        Integer attendeeCount,

        @Size(max = 500, message = "Notes cannot exceed 500 characters")
        String notes
) {}
