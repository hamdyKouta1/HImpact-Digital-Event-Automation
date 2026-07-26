package com.himpact.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request body for mobile phone verification.
 * The guest submits their phone number to receive an OTP.
 *
 * See: project-index/07_API_Specification.md — POST /auth/verify-phone
 * See: project-index/02_Decision_Log.md — DEC-013 Authentication
 */
public record PhoneVerificationRequest(
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in E.164 format (e.g. +201012345678)")
        String phoneNumber,

        /** OTP code — present only when submitting the code received via SMS. */
        String otpCode
) {}
