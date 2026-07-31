package com.himpact.entity;

/**
 * Upload status for media files.
 * See: project-index/06_Database_Design.md — media_files entity
 */
public enum UploadStatus {
    PENDING,
    UPLOADING,
    COMPLETED,
    FAILED,
    DELETED
}
