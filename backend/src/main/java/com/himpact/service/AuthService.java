package com.himpact.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.himpact.config.AppProperties;
import com.himpact.dto.auth.AuthTokenResponse;
import com.himpact.dto.auth.GoogleLoginRequest;
import com.himpact.entity.User;
import com.himpact.entity.UserRole;
import com.himpact.entity.UserStatus;
import com.himpact.exception.AuthenticationException;
import com.himpact.repository.UserRepository;
import com.himpact.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Collections;

/**
 * Authentication Service — handles Google OAuth2 login and JWT token issuance.
 *
 * Flow:
 *  1. Frontend receives Google ID token via Google Sign-In SDK.
 *  2. Frontend sends ID token to POST /api/v1/auth/google.
 *  3. This service verifies the token with Google's servers.
 *  4. User is created or updated in the database.
 *  5. HImpact JWT access + refresh tokens are returned.
 *
 * Business rules:
 *  - New users are created with GUEST role by default.
 *  - Suspended users are rejected.
 *  - Mobile verification is required before accessing event features (enforced downstream).
 *
 * See: project-index/07_API_Specification.md — Authentication APIs
 * See: project-index/02_Decision_Log.md — DEC-013 Authentication
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AppProperties appProperties;

    /**
     * Verify a Google ID token and return HImpact JWT tokens.
     *
     * @param request contains the Google ID token from the client
     * @return HImpact access + refresh token pair
     * @throws AuthenticationException if the Google token is invalid or the user is suspended
     */
    @Transactional
    public AuthTokenResponse googleLogin(GoogleLoginRequest request) {
        GoogleIdToken.Payload payload = verifyGoogleToken(request.idToken());

        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");

        User user = userRepository.findByGoogleId(googleId)
                .map(existing -> updateExistingUser(existing, name, picture))
                .orElseGet(() -> createNewUser(googleId, email, name, picture));

        if (user.getStatus() == UserStatus.SUSPENDED) {
            log.warn("Suspended user [{}] attempted login", email);
            throw new AuthenticationException("Account is suspended. Please contact support.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                user.getId(), user.getEmail(), user.getRole().name());

        log.info("User [{}] authenticated successfully with role [{}]", email, user.getRole());

        return new AuthTokenResponse(
                accessToken,
                refreshToken,
                appProperties.getSecurity().getJwt().getExpirationMs(),
                user.getRole().name(),
                user.isMobileVerified()
        );
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private GoogleIdToken.Payload verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(
                            appProperties.getGoogle().getClientId()))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new AuthenticationException("Invalid Google ID token.");
            }
            return idToken.getPayload();

        } catch (GeneralSecurityException | IOException ex) {
            log.error("Google token verification failed: {}", ex.getMessage());
            throw new AuthenticationException("Google authentication failed. Please try again.");
        }
    }

    private User createNewUser(String googleId, String email, String name, String picture) {
        User newUser = User.builder()
                .googleId(googleId)
                .email(email)
                .fullName(name != null ? name : email)
                .profilePicture(picture)
                .role(UserRole.GUEST)
                .status(UserStatus.PENDING_VERIFICATION)
                .mobileVerified(false)
                .lastLogin(Instant.now())
                .build();

        User saved = userRepository.save(newUser);
        log.info("New user created: [{}] with role GUEST", email);
        return saved;
    }

    private User updateExistingUser(User user, String name, String picture) {
        if (name != null) {
            user.setFullName(name);
        }
        if (picture != null) {
            user.setProfilePicture(picture);
        }
        user.setLastLogin(Instant.now());
        return userRepository.save(user);
    }
}
