package com.himpact.dto.media;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SyncMediaRequest(
        @NotBlank(message = "Invitation code is required")
        String invitationCode,

        @NotBlank(message = "Local identifier is required")
        String localIdentifier,

        @NotBlank(message = "Filename is required")
        String originalFilename,

        @NotBlank(message = "MIME type is required")
        String mimeType,

        @NotNull(message = "File size is required")
        Long fileSize
) {}
