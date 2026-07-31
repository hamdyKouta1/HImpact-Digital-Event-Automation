# 10. Deployment & DevOps — Storage & Credentials Supplement

## Storage Configuration & Google Drive Credentials Management

### 1. Environment Variable Configuration
- `HIMPACT_STORAGE_PROVIDER`: Primary storage provider (`LOCAL` or `GOOGLE_DRIVE`).
- `HIMPACT_STORAGE_LOCAL_ROOT`: Base directory for local disk uploads (Default: `./uploads`).
- `GOOGLE_DRIVE_SERVICE_ACCOUNT_JSON`: Base64 / raw JSON string containing Google Service Account credentials.
- `GOOGLE_DRIVE_ROOT_FOLDER_ID`: Parent folder ID in Google Drive dedicated account.

### 2. Backup & Disaster Recovery Policy
- **Local Disk Uploads**: Nightly volume snapshot & rsync sync to secondary S3 glacier bucket.
- **Google Drive Storage**: Account credentials transferred securely to customer post-event.
