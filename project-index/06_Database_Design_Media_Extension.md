# 06. Database Design — Media & Storage Indexing & Retention Supplement

## 1. Entity Indexing Strategy

### `media_files`
- `idx_media_event_id` ON `media_files (event_id)`
- `idx_media_guest_id` ON `media_files (guest_id)`
- `idx_media_upload_status` ON `media_files (upload_status)`
- `idx_media_event_guest` ON `media_files (event_id, guest_id)`

### `media_sync`
- `idx_media_sync_guest_status` ON `media_sync (guest_id, sync_status)`
- `idx_media_sync_event_status` ON `media_sync (event_id, sync_status)`
- `idx_media_sync_last_retry` ON `media_sync (last_retry_at) WHERE sync_status = 'FAILED'`
- `uq_media_sync_local_id` UNIQUE ON `media_sync (guest_id, local_identifier)`

### `storage_connections` (Renamed from `storage_providers` in V14)
- `idx_storage_conn_event_id` ON `storage_connections (event_id)`

## 2. Retention & Cleanup Strategy

- **Soft Delete Retention**: Deleted media files (`is_deleted = true` / `upload_status = 'DELETED'`) are retained for 30 days before permanent physical purge.
- **Failed Sync Purge**: `media_sync` records in `FAILED` / `DEAD_LETTER` state older than 90 days are archived to secondary cold storage.
