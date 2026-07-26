package com.himpact.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Local File System implementation of StorageProvider.
 * Used for local development and integration testing.
 */
@Slf4j
@Component("localStorageProvider")
public class LocalStorageProvider implements StorageProvider {

    private final Path baseLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public LocalStorageProvider() {
        try {
            Files.createDirectories(baseLocation);
        } catch (IOException ex) {
            log.error("Could not create local upload directory", ex);
        }
    }

    @Override
    public UploadResult upload(String folderPath, String filename, byte[] content, String mimeType) {
        try {
            Path targetFolder = baseLocation.resolve(folderPath).normalize();
            Files.createDirectories(targetFolder);

            String storageFilename = UUID.randomUUID() + "_" + filename;
            Path targetPath = targetFolder.resolve(storageFilename);

            Files.write(targetPath, content);
            log.info("Stored file locally at: {}", targetPath);

            return UploadResult.success(
                    targetPath.toString(),
                    storageFilename,
                    getProviderName(),
                    content.length,
                    mimeType
            );
        } catch (IOException ex) {
            log.error("Local storage upload failed", ex);
            return UploadResult.failure(getProviderName(), ex.getMessage());
        }
    }

    @Override
    public DownloadResult download(String storagePath) {
        try {
            Path filePath = Paths.get(storagePath).normalize();
            if (!Files.exists(filePath)) {
                return DownloadResult.failure("File not found at path: " + storagePath);
            }

            byte[] data = Files.readAllBytes(filePath);
            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            return DownloadResult.success(
                    new ByteArrayInputStream(data),
                    filePath.getFileName().toString(),
                    mimeType,
                    data.length
            );
        } catch (IOException ex) {
            log.error("Local storage download failed", ex);
            return DownloadResult.failure(ex.getMessage());
        }
    }

    @Override
    public void delete(String storagePath) {
        try {
            Path filePath = Paths.get(storagePath).normalize();
            Files.deleteIfExists(filePath);
            log.info("Deleted local file at: {}", storagePath);
        } catch (IOException ex) {
            log.error("Failed to delete local file at: {}", storagePath, ex);
        }
    }

    @Override
    public boolean exists(String storagePath) {
        return Files.exists(Paths.get(storagePath));
    }

    @Override
    public String getProviderName() {
        return "LOCAL";
    }
}
