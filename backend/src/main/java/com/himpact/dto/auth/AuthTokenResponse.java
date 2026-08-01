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
        boolean mobileVerified) {
    public AuthTokenResponse(String accessToken, String refreshToken, long expiresInMs, String role,
            boolean mobileVerified) {
        this(accessToken, refreshToken, expiresInMs / 1000, "Bearer", role, mobileVerified);
    }

    /**
     * Static factory for the refresh-token flow.
     *
     * @param accessToken  new short-lived access token
     * @param refreshToken new long-lived refresh token
     * @param expiresIn    access-token lifetime in seconds
     */
    public static AuthTokenResponse of(String accessToken, String refreshToken, long expiresIn) {
        return new AuthTokenResponse(accessToken, refreshToken, expiresIn, "Bearer", "UNKNOWN", false);
    }
}
