package com.himpact.security;

import com.himpact.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for JwtTokenProvider.
 * Tests token generation, validation, and claim extraction in isolation.
 */
@DisplayName("JwtTokenProvider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private AppProperties appProperties;

    @BeforeEach
    void setUp() {
        appProperties = new AppProperties();
        AppProperties.Security security = new AppProperties.Security();
        AppProperties.Security.Jwt jwt = new AppProperties.Security.Jwt();

        // 256-bit Base64-encoded secret for testing
        jwt.setSecret(Base64.getEncoder().encodeToString(
                "himpact-test-secret-key-for-jwt-signing-256bits".getBytes()));
        jwt.setExpirationMs(3600000L);
        jwt.setRefreshExpirationMs(604800000L);
        security.setJwt(jwt);
        appProperties.setSecurity(security);

        jwtTokenProvider = new JwtTokenProvider(appProperties);
    }

    @Test
    @DisplayName("should generate a valid access token")
    void shouldGenerateValidAccessToken() {
        UUID userId = UUID.randomUUID();
        String email = "test@himpact.app";
        String role = "GUEST";

        String token = jwtTokenProvider.generateAccessToken(userId, email, role);

        assertThat(token).isNotBlank();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("should extract correct userId from token")
    void shouldExtractUserId() {
        UUID userId = UUID.randomUUID();
        String token = jwtTokenProvider.generateAccessToken(userId, "test@example.com", "GUEST");

        UUID extracted = jwtTokenProvider.extractUserId(token);

        assertThat(extracted).isEqualTo(userId);
    }

    @Test
    @DisplayName("should extract correct email from token")
    void shouldExtractEmail() {
        String email = "hamdy@himpact.app";
        String token = jwtTokenProvider.generateAccessToken(UUID.randomUUID(), email, "OWNER");

        assertThat(jwtTokenProvider.extractEmail(token)).isEqualTo(email);
    }

    @Test
    @DisplayName("should extract correct role from token")
    void shouldExtractRole() {
        String token = jwtTokenProvider.generateAccessToken(UUID.randomUUID(), "admin@himpact.app", "ADMIN");

        assertThat(jwtTokenProvider.extractRole(token)).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("should reject an invalid (tampered) token")
    void shouldRejectInvalidToken() {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.invalid.signature";

        assertThat(jwtTokenProvider.validateToken(invalidToken)).isFalse();
    }

    @Test
    @DisplayName("should reject a blank token")
    void shouldRejectBlankToken() {
        assertThat(jwtTokenProvider.validateToken("")).isFalse();
        assertThat(jwtTokenProvider.validateToken(null)).isFalse();
    }
}
