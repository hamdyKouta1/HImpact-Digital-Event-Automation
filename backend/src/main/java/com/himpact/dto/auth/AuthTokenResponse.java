package com.himpact.dto.auth;

/**
 * Response returned after successful Google authentication.
 * Contains short-lived access token and long-lived refresh token.
 *
 * See: project-index/07_API_Specification.md — POST /auth/google response
 */
public record AuthTokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        String tokenType,
        String role,
        boolean mobileVerified
) {
    public AuthTokenResponse(String accessToken, String refreshToken, long expiresInMs, String role, boolean mobileVerified) {
        this(accessToken, refreshToken, expiresInMs / 1000, "Bearer", role, mobileVerified);
    }
}
