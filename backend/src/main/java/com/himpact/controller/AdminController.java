package com.himpact.controller;

import com.himpact.dto.PageResponse;
import com.himpact.entity.AuditLog;
import com.himpact.entity.DashboardStatistics;
import com.himpact.entity.FeatureFlag;
import com.himpact.entity.User;
import com.himpact.entity.UserRole;
import com.himpact.security.HimpactUserPrincipal;
import com.himpact.service.AdminService;
import com.himpact.service.AuditLogService;
import com.himpact.service.FeatureFlagService;
import com.himpact.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Admin Platform Controller.
 * Method-level security enforces fine-grained admin roles: SUPER_ADMIN, ADMIN, FINANCE, SUPPORT.
 *
 * Base path: /api/v1/admin
 * See: project-index/07_API_Specification.md — Admin APIs
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Platform", description = "Platform management, role administration, feature flags, and audit logs")
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('FINANCE') or hasRole('SUPPORT')")
public class AdminController {

    private final AdminService adminService;
    private final FeatureFlagService featureFlagService;
    private final AuditLogService auditLogService;

    @Operation(summary = "Admin Platform Overview", description = "Returns pre-aggregated platform metrics and statistics.")
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getAdminOverview() {
        DashboardStatistics stats = adminService.getAdminOverview();
        return ResponseEntity.ok(ApiResponse.success("Admin overview retrieved.", stats));
    }

    @Operation(summary = "User Management", description = "Paginated list of platform users.")
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<User> users = adminService.listUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully.", users));
    }

    @Operation(summary = "Update User Role", description = "Admin endpoint for promoting/changing user RBAC roles.")
    @PutMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateUserRole(
            @PathVariable UUID userId,
            @RequestParam UserRole newRole,
            @AuthenticationPrincipal HimpactUserPrincipal principal
    ) {
        User updated = adminService.updateUserRole(userId, newRole, principal.userId());
        return ResponseEntity.ok(ApiResponse.success("User role updated successfully.", updated));
    }

    @Operation(summary = "Feature Flags", description = "Returns dynamic feature flag configurations.")
    @GetMapping("/flags")
    public ResponseEntity<Map<String, Object>> getFeatureFlags() {
        List<FeatureFlag> flags = featureFlagService.getAllFlags();
        return ResponseEntity.ok(ApiResponse.success("Feature flags retrieved.", flags));
    }

    @Operation(summary = "Toggle Feature Flag", description = "Enables or disables a runtime feature flag.")
    @PostMapping("/flags/{flagName}/toggle")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleFeatureFlag(
            @PathVariable String flagName,
            @RequestParam boolean enabled
    ) {
        FeatureFlag flag = featureFlagService.toggleFlag(flagName, enabled);
        return ResponseEntity.ok(ApiResponse.success("Feature flag updated.", flag));
    }

    @Operation(summary = "Immutable Audit Logs", description = "Paginated administrative audit trail logs.")
    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<AuditLog> auditLogs = auditLogService.getAuditLogs(pageable);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved.", auditLogs));
    }
}
