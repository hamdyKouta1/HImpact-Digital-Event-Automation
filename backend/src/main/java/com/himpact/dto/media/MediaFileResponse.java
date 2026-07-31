package com.himpact.dto.media;

import com.himpact.entity.UploadStatus;

import java.time.Instant;
import java.util.UUID;

public record MediaFileResponse(
        UUID id,
        UUID eventId,
        UUID guestId,
        String guestName,
        String originalFilename,
        String mimeType,
        long fileSize,
        String storageProvider,
        String storagePath,
        UploadStatus uploadStatus,
        Instant uploadedAt
) {}
