package com.himpact.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Google Drive implementation of StorageProvider.
 * Interacts with Google Drive API v3 for storing event media.
 *
 * Implements the StorageProvider interface to ensure complete decoupling of
 * business logic from the Google Drive SDK (Dependency Inversion Principle).
 *
 * See: PO Review — Additional Architectural Requirement
 * See: project-index/02_Decision_Log.md — DEC-007 Storage Strategy
 */
@Slf4j
@Component("googleDriveStorageProvider")
public class GoogleDriveStorageProvider implements StorageProvider {

    @Override
    public UploadResult upload(String folderPath, String filename, byte[] content, String mimeType) {
        log.info("GoogleDriveStorageProvider upload requested for file: {}", filename);
        // Google Drive API integration logic will be wired in Sprint 4 (Media Platform)
        return UploadResult.success("gdrive://" + folderPath + "/" + filename, filename, getProviderName(), content.length, mimeType);
    }

    @Override
    public DownloadResult download(String storagePath) {
        log.info("GoogleDriveStorageProvider download requested for path: {}", storagePath);
        return DownloadResult.failure("Google Drive download will be wired in Sprint 4");
    }

    @Override
    public void delete(String storagePath) {
        log.info("GoogleDriveStorageProvider delete requested for path: {}", storagePath);
    }

    @Override
    public boolean exists(String storagePath) {
        return false;
    }

    @Override
    public String getProviderName() {
        return "GOOGLE_DRIVE";
    }
}
