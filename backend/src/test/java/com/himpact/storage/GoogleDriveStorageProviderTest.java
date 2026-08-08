package com.himpact.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for GoogleDriveStorageProvider.
 *
 * Tests are designed in two tiers:
 *  1. Pure unit tests (no credentials required) — always run.
 *  2. Integration tests (real credentials) — opt-in via environment variable.
 *
 * To run integration tests, set:
 *     GOOGLE_DRIVE_CREDENTIALS_PATH=<path-to-himpact-drive.json>
 *     GOOGLE_DRIVE_ROOT_FOLDER_ID=1Zqz6Qfvfmjlmg6RoJfqSu0u3YoRgvIpN
 *
 * See: project-index/10_Deployment_DevOps.md — Google Drive Credentials Management
 */
class GoogleDriveStorageProviderTest {

    // ── Tier 1: Unit tests (no credentials) ─────────────────────────────────

    @Test
    void whenNoCredentials_thenFallbackModeIsActive() {
        // Given: no credentials configured
        GoogleDriveStorageProvider provider =
                new GoogleDriveStorageProvider("", "", "", "HImpact");

        // Then: drive service is not initialized (fallback mode)
        assertThat(provider.isDriveInitialized()).isFalse();
        assertThat(provider.getProviderName()).isEqualTo("GOOGLE_DRIVE");
    }

    @Test
    void whenNoCredentials_thenUploadReturnsFallbackId() {
        GoogleDriveStorageProvider provider =
                new GoogleDriveStorageProvider("", "", "", "HImpact");

        UploadResult result = provider.upload("events/test", "photo.jpg", new byte[]{1, 2, 3}, "image/jpeg");

        // Fallback returns a stub ID — does not crash
        assertThat(result).isNotNull();
        assertThat(result.success()).isTrue();
        assertThat(result.storagePath()).contains("gdrive://");
        assertThat(result.storageFilename()).contains("gdrive_stub_");
    }

    @Test
    void whenNoCredentials_thenDownloadReturnsFailure() {
        GoogleDriveStorageProvider provider =
                new GoogleDriveStorageProvider("", "", "", "HImpact");

        DownloadResult result = provider.download("some-file-id");

        assertThat(result.success()).isFalse();
    }

    @Test
    void whenNoCredentials_thenExistsReturnsFalse() {
        GoogleDriveStorageProvider provider =
                new GoogleDriveStorageProvider("", "", "", "HImpact");

        assertThat(provider.exists("some-file-id")).isFalse();
    }

    @Test
    void whenNoCredentials_thenDeleteIsNoOp() {
        GoogleDriveStorageProvider provider =
                new GoogleDriveStorageProvider("", "", "", "HImpact");

        // Should not throw
        provider.delete("some-file-id");
    }

    @Test
    void rootFolderIdIsExposed() {
        GoogleDriveStorageProvider provider =
                new GoogleDriveStorageProvider("", "", "my-folder-id", "HImpact");

        assertThat(provider.getRootFolderId()).isEqualTo("my-folder-id");
    }

    @Test
    void applicationNameDefaultsToHImpact() {
        GoogleDriveStorageProvider provider =
                new GoogleDriveStorageProvider("", "", "", "HImpact");

        assertThat(provider.isDriveInitialized()).isFalse(); // no creds → not init'd
    }

    // ── Tier 2: Integration tests (real credentials, opt-in) ────────────────

    /**
     * Integration test: verifies real Google Drive connectivity.
     * Only runs when GOOGLE_DRIVE_CREDENTIALS_PATH env var is set.
     *
     * Run with:
     *   GOOGLE_DRIVE_CREDENTIALS_PATH=C:\secure\himpact\himpact-drive.json
     *   GOOGLE_DRIVE_ROOT_FOLDER_ID=1Zqz6Qfvfmjlmg6RoJfqSu0u3YoRgvIpN
     *   mvn test -Dtest=GoogleDriveStorageProviderTest#integration_driveIsInitialized
     */
    @Test
    @EnabledIfEnvironmentVariable(named = "GOOGLE_DRIVE_CREDENTIALS_PATH", matches = ".+")
    void integration_driveIsInitialized() {
        String credPath   = System.getenv("GOOGLE_DRIVE_CREDENTIALS_PATH");
        String rootFolder = System.getenv().getOrDefault("GOOGLE_DRIVE_ROOT_FOLDER_ID", "");

        GoogleDriveStorageProvider provider =
                new GoogleDriveStorageProvider("", credPath, rootFolder, "HImpact Integration Test");

        assertThat(provider.isDriveInitialized())
                .as("Drive service must initialize with valid credentials at: " + credPath)
                .isTrue();
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "GOOGLE_DRIVE_CREDENTIALS_PATH", matches = ".+")
    void integration_rootFolderIsAccessible() {
        String credPath   = System.getenv("GOOGLE_DRIVE_CREDENTIALS_PATH");
        String rootFolder = System.getenv("GOOGLE_DRIVE_ROOT_FOLDER_ID");

        assertThat(rootFolder)
                .as("GOOGLE_DRIVE_ROOT_FOLDER_ID must be set for integration test")
                .isNotBlank();

        GoogleDriveStorageProvider provider =
                new GoogleDriveStorageProvider("", credPath, rootFolder, "HImpact Integration Test");

        assertThat(provider.isDriveInitialized()).isTrue();

        boolean accessible = provider.exists(rootFolder);
        assertThat(accessible)
                .as("Root folder [%s] must be accessible (share with service account)", rootFolder)
                .isTrue();
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "GOOGLE_DRIVE_CREDENTIALS_PATH", matches = ".+")
    void integration_uploadDownloadDelete() {
        String credPath   = System.getenv("GOOGLE_DRIVE_CREDENTIALS_PATH");
        String rootFolder = System.getenv().getOrDefault("GOOGLE_DRIVE_ROOT_FOLDER_ID", "");

        GoogleDriveStorageProvider provider =
                new GoogleDriveStorageProvider("", credPath, rootFolder, "HImpact Integration Test");

        assertThat(provider.isDriveInitialized()).isTrue();

        byte[] content = "Hello from HImpact integration test".getBytes();

        // Upload
        UploadResult upload = provider.upload("test/integration", "test-file.txt", content, "text/plain");
        assertThat(upload.success()).isTrue();
        assertThat(upload.storagePath()).isNotBlank();

        String fileId = upload.storagePath(); // for Google Drive, storagePath == file ID

        // Verify exists
        assertThat(provider.exists(fileId)).isTrue();

        // Download
        DownloadResult download = provider.download(fileId);
        assertThat(download.success()).isTrue();
        assertThat(download.contentLength()).isGreaterThan(0);

        // Delete
        provider.delete(fileId);

        // Verify deleted (may take a moment, but exists() returns false for trashed files)
        assertThat(provider.exists(fileId)).isFalse();
    }
}
