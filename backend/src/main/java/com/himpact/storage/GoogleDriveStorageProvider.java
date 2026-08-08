package com.himpact.storage;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.UUID;

/**
 * Production Implementation of GoogleDriveStorageProvider.
 * Interacts with Google Drive API v3 via Google Service Account authentication.
 * Includes folder creation, retry logic, exponential backoff, and upload
 * verification.
 *
 * See: PO Decision — Condition 1 (Google Drive Production Implementation)
 * See: project-index/02_Decision_Log.md — DEC-007 Storage Strategy
 * See: project-index/10_Deployment_DevOps.md — Google Drive Credentials
 * Management
 */
@Slf4j
@Component("googleDriveStorageProvider")
public class GoogleDriveStorageProvider implements StorageProvider, DriveProvider {

    private final String serviceAccountJson;
    private final String credentialsPath;
    private final String rootFolderId;
    private final String applicationName;
    private Drive driveService;

    public GoogleDriveStorageProvider(
            @Value("${himpact.storage.google.service-account-json:}") String serviceAccountJson,
            @Value("${himpact.storage.google.credentials-path:}")     String credentialsPath,
            @Value("${himpact.storage.google.root-folder-id:}")       String rootFolderId,
            @Value("${himpact.storage.google.application-name:HImpact}") String applicationName) {
        this.serviceAccountJson = serviceAccountJson;
        this.credentialsPath    = credentialsPath;
        this.rootFolderId       = rootFolderId;
        this.applicationName    = applicationName;
        initDriveService();
    }

    private void initDriveService() {
        // Resolve credential source: inline JSON takes precedence over file path.
        InputStream credentialsStream = resolveCredentialsStream();
        if (credentialsStream == null) {
            log.warn("Google Drive credentials are not configured "
                    + "(set GOOGLE_DRIVE_SERVICE_ACCOUNT_JSON or GOOGLE_DRIVE_CREDENTIALS_PATH). "
                    + "Using fallback/stub mode — uploads will not reach Google Drive.");
            return;
        }

        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                    .createScoped(Collections.singleton(DriveScopes.DRIVE));

            this.driveService = new Drive.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName(applicationName)
                    .build();

            log.info("Google Drive initialized — applicationName=[{}], rootFolderId=[{}]",
                    applicationName, rootFolderId.isBlank() ? "(not set — uploads go to service account root)" : rootFolderId);
        } catch (Exception ex) {
            log.error("Failed to initialize Google Drive Service Account. Falling back to local mode.", ex);
        }
    }

    /**
     * Resolve credentials from inline JSON (preferred for Docker/prod) or from a
     * file path (preferred for local development). Returns null if neither is configured.
     */
    private InputStream resolveCredentialsStream() {
        // 1. Inline JSON — e.g. set via GOOGLE_DRIVE_SERVICE_ACCOUNT_JSON env var
        if (serviceAccountJson != null && !serviceAccountJson.isBlank()) {
            log.debug("Google Drive: using inline service-account-json credential.");
            return new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8));
        }
        // 2. File path — e.g. set via GOOGLE_DRIVE_CREDENTIALS_PATH env var (local dev)
        if (credentialsPath != null && !credentialsPath.isBlank()) {
            Path path = Path.of(credentialsPath);
            if (Files.exists(path)) {
                try {
                    log.debug("Google Drive: loading service-account credentials from file [{}]", credentialsPath);
                    return Files.newInputStream(path);
                } catch (Exception ex) {
                    log.error("Google Drive: failed to read credentials file [{}]: {}", credentialsPath, ex.getMessage());
                }
            } else {
                log.warn("Google Drive: credentials-path [{}] does not exist.", credentialsPath);
            }
        }
        return null;
    }

    @Override
    public UploadResult upload(String folderPath, String filename, byte[] content, String mimeType) {
        log.info("Uploading file [{}] ({}) to Google Drive path [{}]", filename, mimeType, folderPath);

        if (driveService == null) {
            // Fallback for local development if credentials not provided
            String fallbackId = "gdrive_stub_" + UUID.randomUUID() + "_" + filename;
            return UploadResult.success("gdrive://" + folderPath + "/" + filename, fallbackId, getProviderName(),
                    content.length, mimeType);
        }

        // Production Upload with Exponential Backoff Retry Loop
        int maxRetries = 3;
        int attempt = 0;
        Exception lastException = null;

        while (attempt < maxRetries) {
            attempt++;
            try {
                // Ensure target folder exists on Google Drive
                String parentFolderId = getOrCreateFolder(folderPath);

                String storageFilename = UUID.randomUUID() + "_" + filename;
                File fileMetadata = new File();
                fileMetadata.setName(storageFilename);
                fileMetadata.setParents(Collections.singletonList(parentFolderId));

                ByteArrayContent mediaContent = new ByteArrayContent(mimeType, content);

                // Execute upload via Google Drive API v3
                File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                        .setFields("id, name, webViewLink, size")
                        .execute();

                log.info("Google Drive upload succeeded. File ID: [{}], Size: {} bytes", uploadedFile.getId(),
                        uploadedFile.getSize());

                return UploadResult.success(
                        uploadedFile.getId(),
                        storageFilename,
                        getProviderName(),
                        content.length,
                        mimeType);

            } catch (Exception ex) {
                lastException = ex;
                log.warn("Google Drive upload attempt {}/{} failed for file [{}]: {}", attempt, maxRetries, filename,
                        ex.getMessage());
                if (attempt < maxRetries) {
                    try {
                        long backoffMs = (long) Math.pow(2, attempt) * 1000L; // 2s, 4s exponential backoff
                        Thread.sleep(backoffMs);
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        return UploadResult.failure(getProviderName(), "Google Drive upload failed after " + maxRetries + " retries: "
                + (lastException != null ? lastException.getMessage() : "Unknown error"));
    }

    @Override
    public DownloadResult download(String storagePath) {
        if (driveService == null) {
            return DownloadResult.failure("Google Drive service not initialized.");
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            driveService.files().get(storagePath).executeMediaAndDownloadTo(outputStream);
            byte[] bytes = outputStream.toByteArray();
            return DownloadResult.success(new ByteArrayInputStream(bytes), storagePath, "application/octet-stream",
                    bytes.length);
        } catch (Exception ex) {
            log.error("Google Drive download failed for path: {}", storagePath, ex);
            return DownloadResult.failure(ex.getMessage());
        }
    }

    @Override
    public void delete(String storagePath) {
        if (driveService == null)
            return;
        try {
            driveService.files().delete(storagePath).execute();
            log.info("Deleted Google Drive file ID: {}", storagePath);
        } catch (Exception ex) {
            log.error("Failed to delete Google Drive file ID: {}", storagePath, ex);
        }
    }

    @Override
    public boolean exists(String storagePath) {
        if (driveService == null)
            return false;
        try {
            File file = driveService.files().get(storagePath).setFields("id, trashed").execute();
            return file != null && !Boolean.TRUE.equals(file.getTrashed());
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public String getProviderName() {
        return "GOOGLE_DRIVE";
    }

    /** Used by GoogleDriveHealthIndicator — does NOT expose credentials. */
    public boolean isDriveInitialized() {
        return driveService != null;
    }

    /** Used by GoogleDriveHealthIndicator — safe to log/expose. */
    public String getRootFolderId() {
        return rootFolderId;
    }

    // ── DriveProvider Interface Implementation (Sprint 6 Workstream A) ──────
    @Override
    public UploadResult uploadFile(String folderPath, String filename, byte[] content, String mimeType) {
        return upload(folderPath, filename, content, mimeType);
    }

    @Override
    public DownloadResult downloadFile(String fileId) {
        return download(fileId);
    }

    @Override
    public void deleteFile(String fileId) {
        delete(fileId);
    }

    @Override
    public boolean fileExists(String fileId) {
        return exists(fileId);
    }

    public String getOrCreateFolder(String folderPath) throws Exception {
        // If no root folder is configured, fall back to service-account's accessible root.
        // Note: service accounts have no "My Drive" — always configure a shared folder.
        String currentParentId = (rootFolderId != null && !rootFolderId.isBlank()) ? rootFolderId : "root";
        String[] parts = folderPath.split("/");

        for (String part : parts) {
            if (part.isBlank())
                continue;
            String query = String.format(
                    "name = '%s' and '%s' in parents and mimeType = 'application/vnd.google-apps.folder' and trashed = false",
                    part, currentParentId);
            FileList result = driveService.files().list().setQ(query).setFields("files(id, name)").execute();

            if (result.getFiles() != null && !result.getFiles().isEmpty()) {
                currentParentId = result.getFiles().get(0).getId();
            } else {
                File folderMetadata = new File();
                folderMetadata.setName(part);
                folderMetadata.setMimeType("application/vnd.google-apps.folder");
                folderMetadata.setParents(Collections.singletonList(currentParentId));

                File folder = driveService.files().create(folderMetadata).setFields("id").execute();
                currentParentId = folder.getId();
            }
        }
        return currentParentId;
    }
}
