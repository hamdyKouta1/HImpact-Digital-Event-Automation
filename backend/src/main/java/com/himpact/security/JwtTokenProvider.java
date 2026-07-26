package com.himpact.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.himpact.config.AppProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * JWT Token Provider — issues and validates JWT access and refresh tokens.
 *
 * Tokens are signed with HMAC-SHA256 using the secret from application config.
 * All secrets are loaded from environment variables — never hardcoded.
 *
 * See: project-index/07_API_Specification.md — Authentication APIs
 * See: project-index/05_Software_Architecture.md — Security Architecture
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final AppProperties appProperties;

    /**
     * Generate a short-lived access token for the authenticated user.
     */
    public String generateAccessToken(UUID userId, String email, String role) {
        return buildToken(userId, email, role, appProperties.getSecurity().getJwt().getExpirationMs());
    }

    /**
     * Generate a long-lived refresh token for the authenticated user.
     */
    public String generateRefreshToken(UUID userId, String email, String role) {
        return buildToken(userId, email, role, appProperties.getSecurity().getJwt().getRefreshExpirationMs());
    }

    /**
     * Validate a JWT token. Returns true if the token is valid and not expired.
     */
    public boolean validateToken(String token) {
        try {
            getParser().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.warn("JWT token expired: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.warn("JWT token unsupported: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.warn("JWT token malformed: {}", ex.getMessage());
        } catch (SecurityException ex) {
            log.warn("JWT signature invalid: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.warn("JWT claims string empty: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Extract the user ID (subject) from a validated token.
     */
    public UUID extractUserId(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    /**
     * Extract the user email from a validated token.
     */
    public String extractEmail(String token) {
        return getClaims(token).get("email", String.class);
    }

    /**
     * Extract the user role from a validated token.
     */
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private String buildToken(UUID userId, String email, String role, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(userId.toString())
                .claims(Map.of(
                        "email", email,
                        "role", role
                ))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    private Claims getClaims(String token) {
        return getParser().parseSignedClaims(token).getPayload();
    }

    private JwtParser getParser() {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(appProperties.getSecurity().getJwt().getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
