package com.himpact.storage;

import java.io.InputStream;

/**
 * Result returned when downloading a file from a storage provider.
 */
public record DownloadResult(
        InputStream inputStream,
        String filename,
        String mimeType,
        long contentLength,
        boolean success,
        String errorMessage
) {
    public static DownloadResult success(InputStream inputStream, String filename, String mimeType, long contentLength) {
        return new DownloadResult(inputStream, filename, mimeType, contentLength, true, null);
    }

    public static DownloadResult failure(String errorMessage) {
        return new DownloadResult(null, null, null, 0, false, errorMessage);
    }
}
