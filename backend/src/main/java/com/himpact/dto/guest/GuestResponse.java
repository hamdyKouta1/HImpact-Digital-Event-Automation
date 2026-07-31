package com.himpact.dto.guest;

import com.himpact.entity.GuestStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Detailed guest response returned by API endpoints.
 * See: project-index/07_API_Specification.md — Guest APIs
 */
public record GuestResponse(
        UUID id,
        UUID eventId,
        String fullName,
        String mobile,
        String email,
        String invitationCode,
        String invitationUrl,
        int uploadLimit,
        int uploadedCount,
        BigDecimal storageUsedMb,
        GuestStatus status,
        Instant createdAt
) {}
