package com.himpact.entity;

/**
 * Synchronization status for media_sync entity.
 * See: project-index/06_Database_Design.md — media_sync entity
 */
public enum SyncStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}
