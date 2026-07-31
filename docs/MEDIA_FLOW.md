# Media Upload, Storage Provider & Offline Sync Pipeline

## 1. Media Pipeline Overview

The **Media Management Subsystem** handles guest photo and video uploads, enforcing file format security, guest quota limits, local disk storage, transaction-rollback file cleanup, and background cloud synchronization to **Google Drive**.

### Key Architectural Capabilities
- **Server-Side MIME & Size Validation**: Accepts `JPG`, `PNG`, `WEBP`, `HEIC` (max 20MB) and `MP4`, `MOV` (max 200MB).
- **Dual-Storage Provider Pattern**: Immediate local disk write (`LocalStorageProvider`) for zero-latency response, followed by asynchronous cloud backup (`GoogleDriveStorageProvider`).
- **Transaction Rollback Protection**: If the database save fails after storing a file on disk, `MediaService` automatically deletes the file from disk to prevent storage leaks.
- **Offline Queue Deduplication**: Public clients upload offline queues with a `localIdentifier`. Idempotent `MediaSync` records prevent duplicate file processing upon reconnection.

---

## 2. Media Upload & Storage Sync Sequence Diagram

```mermaid
sequenceDiagram
    autonumber
    actor Guest as Client Web/Mobile App
    participant Ctrl as MediaController
    participant Service as MediaService
    participant LocalStore as LocalStorageProvider
    participant DB as PostgreSQL DB
    participant Bus as ApplicationEventPublisher
    participant Listener as StorageListener
    participant GDrive as GoogleDriveStorageProvider
    participant DriveAPI as Google Drive API v3

    Guest->>Ctrl: POST /api/v1/events/{id}/media (file, invitationCode, localIdentifier)
    Ctrl->>Service: uploadMedia(eventId, invitationCode, file, localIdentifier)
    
    Service->>Service: Validate MIME type & file size limit
    Service->>Service: Verify guest invitation code & remaining upload quota

    Service->>LocalStore: upload(folderPath, filename, content, mimeType)
    LocalStore-->>Service: UploadResult (storagePath: "uploads/events/...")

    alt DB Save Succeeds
        Service->>DB: Save MediaFile & MediaSync records, update Guest counters
        DB-->>Service: Transaction Committed
        Service->>Bus: publishEvent(MediaUploadedEvent)
    else DB Save Fails (Exception)
        Service->>LocalStore: delete(storagePath) [Rollback Cleanup]
        Service-->>Guest: 500 Error (Transaction Rolled Back)
    end

    par Async Background Cloud Sync (AFTER_COMMIT)
        Bus->>Listener: handleMediaUploaded(MediaUploadedEvent)
        Listener->>GDrive: upload(folderPath, filename, content, mimeType)
        loop Up to 3 Retries with Exponential Backoff (2s, 4s)
            GDrive->>DriveAPI: files().create(fileMetadata, mediaContent)
            DriveAPI-->>GDrive: File Created (Google Drive File ID)
        end
        GDrive-->>Listener: UploadResult Success
        Listener->>DB: UPDATE media_sync SET sync_status = 'SYNCED'
    end

    Service-->>Ctrl: MediaFileResponse (201 Created)
    Ctrl-->>Guest: 201 Created JSON
```

---

## 3. Storage Provider Architecture

```mermaid
classDiagram
    class StorageProvider {
        <<interface>>
        +upload(String folderPath, String filename, byte[] content, String mimeType) UploadResult
        +download(String storagePath) DownloadResult
        +delete(String storagePath) void
        +exists(String storagePath) boolean
        +getProviderName() String
    }

    class DriveProvider {
        <<interface>>
        +uploadFile(String folderPath, String filename, byte[] content, String mimeType) UploadResult
        +downloadFile(String fileId) DownloadResult
        +deleteFile(String fileId) void
        +fileExists(String fileId) boolean
    }

    class LocalStorageProvider {
        -String baseUploadDir
        +upload(...) UploadResult
        +download(...) DownloadResult
        +delete(...) void
        +exists(...) boolean
    }

    class GoogleDriveStorageProvider {
        -Drive driveService
        -String serviceAccountJson
        -String rootFolderId
        +upload(...) UploadResult
        +download(...) DownloadResult
        +delete(...) void
        +exists(...) boolean
        -getOrCreateFolder(String folderPath) String
    }

    StorageProvider <|.. LocalStorageProvider
    StorageProvider <|.. GoogleDriveStorageProvider
    DriveProvider <|.. GoogleDriveStorageProvider
```

---

## 4. Offline Queue & Idempotent Synchronization (`SyncMediaRequest`)

When a mobile or web client records media while offline, it queues the files locally with a generated `localIdentifier` (e.g. `client_uuid_12345`).

Upon reconnecting, the client calls `POST /api/v1/events/{id}/media/sync`:
1. `MediaService` queries `MediaSyncRepository` by `guestId` and `localIdentifier`.
2. If a record exists with `SyncStatus.COMPLETED`, the service bypasses processing and immediately returns the existing `MediaFileResponse`.
3. If no record exists, a new `MediaSync` record is created in state `PENDING`, allowing the client to proceed with uploading the payload without creating duplicates.
