package com.himpact.storage;

/**
 * Storage Provider Abstraction Interface.
 *
 * Business logic must NEVER depend directly on Google Drive, S3, or local storage.
 * All media operations interact exclusively through this interface.
 *
 * Implementations:
 *  - LocalStorageProvider (Development / Testing)
 *  - GoogleDriveStorageProvider (MVP Production)
 *  - S3StorageProvider / AzureBlobStorageProvider (Future releases)
 *
 * See: PO Review — Additional Architectural Requirement (Dependency Inversion Principle)
 * See: project-index/02_Decision_Log.md — DEC-007 Storage Strategy
 */
public interface StorageProvider {

    /**
     * Upload a file to the storage provider under a designated folder path.
     */
    UploadResult upload(String folderPath, String filename, byte[] content, String mimeType);

    /**
     * Download a file from the storage provider by its storage path / ID.
     */
    DownloadResult download(String storagePath);

    /**
     * Delete a file from the storage provider by its storage path / ID.
     */
    void delete(String storagePath);

    /**
     * Check if a file exists in the storage provider.
     */
    boolean exists(String storagePath);

    /**
     * Unique identifier for the provider (e.g. GOOGLE_DRIVE, LOCAL, S3).
     */
    String getProviderName();
}
