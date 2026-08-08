package com.himpact.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Google Drive health indicator for Spring Boot Actuator.
 *
 * Reports:
 *  - UP   : Drive service is initialized and the root folder is accessible.
 *  - UNKNOWN : Credentials are not configured (acceptable in dev/test environments).
 *  - DOWN : Credentials are configured but Drive is unreachable.
 *
 * This indicator never causes the overall application health to be DOWN
 * solely because Google Drive is not configured — that decision belongs to
 * the readiness probe group configuration in application.yml.
 *
 * See: project-index/10_Deployment_DevOps.md — Health Probes
 */
@Slf4j
@Component("googleDriveHealthIndicator")
@RequiredArgsConstructor
public class GoogleDriveHealthIndicator implements HealthIndicator {

    private final GoogleDriveStorageProvider googleDriveStorageProvider;

    @Override
    public Health health() {
        try {
            boolean initialized = googleDriveStorageProvider.isDriveInitialized();

            if (!initialized) {
                return Health.unknown()
                        .withDetail("status", "Google Drive credentials not configured — running in stub/fallback mode")
                        .withDetail("impact", "File uploads return stub IDs; no actual Drive access")
                        .build();
            }

            // Quick connectivity check: verify root folder is accessible
            String rootFolderId = googleDriveStorageProvider.getRootFolderId();
            if (rootFolderId != null && !rootFolderId.isBlank()) {
                boolean accessible = googleDriveStorageProvider.exists(rootFolderId);
                if (accessible) {
                    return Health.up()
                            .withDetail("rootFolderId", rootFolderId)
                            .withDetail("provider", "GOOGLE_DRIVE")
                            .build();
                } else {
                    return Health.down()
                            .withDetail("error", "Root folder not accessible: " + rootFolderId)
                            .withDetail("hint", "Verify the folder is shared with the service account (Editor role)")
                            .build();
                }
            }

            return Health.up()
                    .withDetail("provider", "GOOGLE_DRIVE")
                    .withDetail("rootFolderId", "(not configured — using service account root)")
                    .build();

        } catch (Exception ex) {
            log.warn("Google Drive health check failed: {}", ex.getMessage());
            return Health.down()
                    .withDetail("error", ex.getMessage())
                    .build();
        }
    }
}
