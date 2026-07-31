package com.himpact.controller;

import com.himpact.dto.invitation.PublicInvitationResponse;
import com.himpact.service.InvitationService;
import com.himpact.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Public Invitation Controller.
 * Base path: /api/v1/invite
 * See: project-index/07_API_Specification.md — Invitation APIs
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/invite")
@RequiredArgsConstructor
@Tag(name = "Guest Invitations", description = "Public guest invitation landing page and view tracking")
public class InvitationController {

    private final InvitationService invitationService;

    @Operation(summary = "Get Public Invitation", description = "Public endpoint fetching event & guest invitation details by slug and code.")
    @GetMapping("/{slug}")
    public ResponseEntity<Map<String, Object>> getPublicInvitation(
            @PathVariable String slug,
            @RequestParam String code
    ) {
        PublicInvitationResponse invitation = invitationService.getPublicInvitation(slug, code);
        return ResponseEntity.ok(ApiResponse.success("Invitation retrieved successfully.", invitation));
    }
}
