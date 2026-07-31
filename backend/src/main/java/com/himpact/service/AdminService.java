package com.himpact.service;

import com.himpact.dto.PageResponse;
import com.himpact.entity.DashboardStatistics;
import com.himpact.entity.User;
import com.himpact.entity.UserRole;
import com.himpact.exception.ResourceNotFoundException;
import com.himpact.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Admin Platform Service.
 * Implements role-based administration, user management, and health monitoring.
 *
 * See: PO Sprint 5 Workstream C Admin Platform
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AnalyticsService analyticsService;
    private final FeatureFlagService featureFlagService;
    private final AuditLogService auditLogService;

    @Transactional(readOnly = true)
    public DashboardStatistics getAdminOverview() {
        return analyticsService.getGlobalPlatformStatistics();
    }

    @Transactional(readOnly = true)
    public PageResponse<User> listUsers(Pageable pageable) {
        return PageResponse.from(userRepository.findAll(pageable));
    }

    @Transactional
    public User updateUserRole(UUID userId, UserRole newRole, UUID adminUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        UserRole oldRole = user.getRole();
        user.setRole(newRole);
        User updated = userRepository.save(user);

        auditLogService.recordAudit(
                adminUserId,
                "ADMIN_PANEL",
                "UPDATE_USER_ROLE",
                "User",
                userId.toString(),
                oldRole.name(),
                newRole.name()
        );

        log.info("Admin [{}] updated role of user [{}] to [{}]", adminUserId, userId, newRole);
        return updated;
    }
}
