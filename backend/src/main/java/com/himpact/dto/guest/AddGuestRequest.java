package com.himpact.dto.guest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request body for adding a single guest to an event.
 * See: project-index/07_API_Specification.md — POST /events/{eventId}/guests
 */
public record AddGuestRequest(
        @NotBlank(message = "Guest full name is required")
        @Size(max = 255, message = "Name cannot exceed 255 characters")
        String fullName,

        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Mobile number must be valid format")
        String mobile,

        @Email(message = "Invalid email format")
        String email,

        Integer uploadLimit
) {}
