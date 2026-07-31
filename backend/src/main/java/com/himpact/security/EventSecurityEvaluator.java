package com.himpact.security;

import com.himpact.entity.UserRole;
import com.himpact.repository.EventRepository;
import com.himpact.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Custom Spring Security expression evaluator for method-level security.
 * Enforces the event isolation policies approved in the Role-Permission Matrix.
 *
 * Usage in controllers:
 *   @PreAuthorize("@eventSecurity.isOwner(#eventId)")
 *   @PreAuthorize("@eventSecurity.isGuestOrOwner(#eventId)")
 *
 * See: project-index/05_Software_Architecture.md — Security Architecture
 * See: Role-Permission Matrix (PI-03-ROLE-MATRIX)
 */
@Slf4j
@Component("eventSecurity")
@RequiredArgsConstructor
public class EventSecurityEvaluator {

    private final EventRepository eventRepository;
    private final GuestRepository guestRepository;

    /**
     * Verify if the authenticated user is the OWNER of the specified event (or an ADMIN).
     */
    @Transactional(readOnly = true)
    public boolean isOwner(UUID eventId) {
        HimpactUserPrincipal principal = getCurrentPrincipal();
        if (principal == null) return false;

        // ADMIN has global access
        if (UserRole.ADMIN.name().equals(principal.role())) {
            return true;
        }

        // Check if event belongs to this user
        return eventRepository.findById(eventId)
                .map(event -> event.getOwner().getId().equals(principal.userId()))
                .orElse(false);
    }

    /**
     * Verify if the authenticated user is an invited GUEST or OWNER of the specified event.
     */
    @Transactional(readOnly = true)
    public boolean isGuestOrOwner(UUID eventId) {
        HimpactUserPrincipal principal = getCurrentPrincipal();
        if (principal == null) return false;

        if (UserRole.ADMIN.name().equals(principal.role())) {
            return true;
        }

        if (isOwner(eventId)) {
            return true;
        }

        // Check if user is an authorized guest for this event
        return guestRepository.findByEventIdAndEmailAndIsDeletedFalse(eventId, principal.email()).isPresent()
                || guestRepository.findByEventIdAndMobileAndIsDeletedFalse(eventId, principal.email()).isPresent();
    }

    private HimpactUserPrincipal getCurrentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof HimpactUserPrincipal principal) {
            return principal;
        }
        return null;
    }
}
