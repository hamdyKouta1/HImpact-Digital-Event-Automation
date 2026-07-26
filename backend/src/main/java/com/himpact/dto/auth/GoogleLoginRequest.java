package com.himpact.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for Google OAuth2 login.
 * The frontend receives an ID token from Google Sign-In and sends it here for verification.
 *
 * See: project-index/07_API_Specification.md — POST /auth/google
 */
public record GoogleLoginRequest(
        @NotBlank(message = "Google ID token is required")
        String idToken
) {}
