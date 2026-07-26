package com.himpact.controller;

import com.himpact.dto.auth.AuthTokenResponse;
import com.himpact.dto.auth.GoogleLoginRequest;
import com.himpact.service.AuthService;
import com.himpact.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authentication Controller — handles all auth endpoints.
 *
 * Controllers contain NO business logic — they delegate to AuthService.
 * Input is validated via @Valid before reaching the service layer.
 *
 * Base path: /api/v1/auth
 * See: project-index/07_API_Specification.md — Authentication APIs
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Google OAuth2 login and token management")
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/v1/auth/google
     * Verify a Google ID token and return HImpact JWT tokens.
     */
    @Operation(
            summary = "Google OAuth2 Login",
            description = "Verifies the Google ID token received from the frontend and returns HImpact JWT access and refresh tokens."
    )
    @PostMapping("/google")
    public ResponseEntity<Map<String, Object>> googleLogin(
            @Valid @RequestBody GoogleLoginRequest request
    ) {
        AuthTokenResponse tokens = authService.googleLogin(request);
        return ResponseEntity.ok(ApiResponse.success("Authentication successful.", tokens));
    }

    /**
     * POST /api/v1/auth/logout
     * Stateless — client discards the token. Endpoint included for API completeness
     * and future refresh token invalidation.
     */
    @Operation(summary = "Logout", description = "Invalidates the session. Client must discard stored tokens.")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        // Stateless JWT — no server-side session to invalidate in MVP.
        // Future: add refresh token revocation to a token blacklist.
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully."));
    }
}
