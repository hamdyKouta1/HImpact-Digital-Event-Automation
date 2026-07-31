# 05. Software Architecture — Media Pipeline & Transaction Safety Supplement

## Media Processing Lifecycle & AFTER_COMMIT Event Model

The HImpact platform guarantees complete transaction safety and zero orphaned storage files through the following architecture:

```
Guest Upload Request (MultipartFile / Client Local Identifier)
  │
  ├── 1. Validation: MIME Type Check (JPG, PNG, WEBP, HEIC, MP4, MOV) & File Size Limits (Photos ≤ 20MB, Videos ≤ 200MB)
  ├── 2. Quota Check: Guest Upload Limit (uploadedCount < uploadLimit) & Event Storage Quota
  ├── 3. Storage Write: StorageProvider.upload(...) -> Stores file on disk / Google Drive API v3
  ├── 4. Database Transaction (@Transactional):
  │     ├── Save MediaFile entity
  │     ├── Increment Guest uploadedCount & storageUsedMb
  │     └── Save MediaSync entity (local_identifier idempotency key)
  │
  ├── [IF DB EXCEPTION / ROLLBACK]:
  │     └── Rollback File Cleanup: Catch Exception -> StorageProvider.delete(storagePath) -> Throw Exception
  │
  └── [IF DB COMMIT SUCCESSFUL]:
        └── Event Publisher: publishEvent(MediaUploadedEvent)
              │
              └── @TransactionalEventListener(phase = AFTER_COMMIT):
                    ├── NotificationListener: Queue Async Notification
                    ├── AnalyticsListener: Update Event Metrics
                    └── AuditListener: Log Security Audit Entry
```

## Target Production Media Processing Pipeline (Architecture Evolution)

```
[Upload] ──> [Temp Storage] ──> [Metadata Extraction] ──> [ClamAV Virus Scan] ──> [Thumbnail Generation] ──> [Image/Video Compression] ──> [Permanent Storage] ──> [AFTER_COMMIT Publish]
```
