# 11. Testing Strategy — Media Stress & Recovery Supplement

## Media Upload & Offline Sync Testing Matrix

| Scenario | Test Tool | Description | Expected Outcome |
|----------|-----------|-------------|------------------|
| **Quota Limit** | JUnit / Playwright | Guest uploads photos exceeding `uploadLimit` | 400 Bad Request ("Upload quota exceeded") |
| **Invalid MIME** | JUnit / Playwright | Guest uploads `.exe` or `.txt` file | 400 Bad Request ("Invalid media type") |
| **Size Exceeded** | JUnit / Playwright | Guest uploads 25MB photo or 250MB video | 400 Bad Request ("File size exceeds limit") |
| **DB Failure Cleanup** | JUnit (Mockito) | DB connection fails during `MediaFile` save | Storage file deleted immediately; zero orphaned files |
| **Offline Reconnect Sync** | Playwright E2E | Offline IndexedDB queueing & online auto-sync | All queued items uploaded; idempotency verified |
| **Bounded Retry Backoff** | Playwright E2E | Server returns 500 error 3 times during sync | Item transitions to `DEAD_LETTER` state after 3 retries; user notified |
