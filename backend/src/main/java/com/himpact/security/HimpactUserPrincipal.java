package com.himpact.security;

import java.util.UUID;

/**
 * Immutable principal stored in the Spring SecurityContext after JWT authentication.
 * Downstream code can extract userId, email, and role without re-parsing the token.
 */
public record HimpactUserPrincipal(
        UUID userId,
        String email,
        String role
) {}
