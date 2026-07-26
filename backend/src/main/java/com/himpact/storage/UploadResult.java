package com.himpact.storage;

/**
 * Result returned after uploading a file to a storage provider.
 */
public record UploadResult(
        String storagePath,
        String storageFilename,
        String providerName,
        long fileSize,
        String mimeType,
        boolean success,
        String errorMessage
) {
    public static UploadResult success(String storagePath, String storageFilename, String providerName, long fileSize, String mimeType) {
        return new UploadResult(storagePath, storageFilename, providerName, fileSize, mimeType, true, null);
    }

    public static UploadResult failure(String providerName, String errorMessage) {
        return new UploadResult(null, null, providerName, 0, null, false, errorMessage);
    }
}
