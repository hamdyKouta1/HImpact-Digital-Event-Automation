package com.himpact.storage;

/**
 * Drive Provider Interface.
 * Production-ready abstraction for Google Drive file management per Sprint 6 Workstream A.
 */
public interface DriveProvider {

    /**
     * Upload a file to Google Drive.
     */
    UploadResult uploadFile(String folderPath, String filename, byte[] content, String mimeType);

    /**
     * Download a file from Google Drive.
     */
    DownloadResult downloadFile(String fileId);

    /**
     * Delete a file from Google Drive.
     */
    void deleteFile(String fileId);

    /**
     * Check if a file exists on Google Drive.
     */
    boolean fileExists(String fileId);

    /**
     * Get or create a folder path on Google Drive.
     */
    String getOrCreateFolder(String folderPath);
}
