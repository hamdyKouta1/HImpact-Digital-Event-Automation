-- V15: Add last_retry_at operational metadata column to media_sync table per PO Requirement 9
-- Greatly improves production troubleshooting and automated retry scheduling.
-- See: project-index/06_Database_Design.md — media_sync entity

ALTER TABLE media_sync ADD COLUMN last_retry_at TIMESTAMPTZ;

CREATE INDEX idx_media_sync_last_retry ON media_sync (last_retry_at) WHERE sync_status = 'FAILED';
