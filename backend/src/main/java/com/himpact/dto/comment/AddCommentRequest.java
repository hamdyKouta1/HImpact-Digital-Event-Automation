package com.himpact.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddCommentRequest(
        @NotBlank(message = "Invitation code is required")
        String invitationCode,

        @NotBlank(message = "Message is required")
        @Size(max = 1000, message = "Message cannot exceed 1000 characters")
        String message
) {}
