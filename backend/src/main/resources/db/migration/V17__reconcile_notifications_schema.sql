-- V17: Reconcile notifications table schema with Notification.java entity
-- The V10 schema diverged from the current entity — this migration aligns them.
--
-- Changes applied:
-- 1. Drop obsolete columns not in entity: notification_type, scheduled_at, delivered_at
-- 2. Add missing columns: recipient, subject, content, retry_count, sent_at
-- 3. Drop CHECK constraints that are now too restrictive (delivery_channel / status)
-- 4. Widen delivery_channel from VARCHAR(20) to VARCHAR(30) to match entity length
-- 5. Widen status from VARCHAR(20) to VARCHAR(30) to match entity length
-- 6. Drop the NOT NULL constraint on event_id (entity marks FK as optional/nullable)
-- 7. Add missing idx_notifications_channel and idx_notifications_created_at indexes

-- Step 1: Drop obsolete check constraints before altering column types
ALTER TABLE notifications
    DROP CONSTRAINT IF EXISTS ck_notifications_channel,
    DROP CONSTRAINT IF EXISTS ck_notifications_status;

-- Step 2: Drop the stale scheduled index that referenced the old status constraint
DROP INDEX IF EXISTS idx_notifications_scheduled;

-- Step 3: Drop columns that no longer exist in the entity
ALTER TABLE notifications
    DROP COLUMN IF EXISTS notification_type,
    DROP COLUMN IF EXISTS scheduled_at,
    DROP COLUMN IF EXISTS delivered_at;

-- Step 4: Add missing columns required by the entity (nullable first, then set defaults)
ALTER TABLE notifications
    ADD COLUMN IF NOT EXISTS recipient      VARCHAR(255),
    ADD COLUMN IF NOT EXISTS subject        VARCHAR(255),
    ADD COLUMN IF NOT EXISTS content        TEXT,
    ADD COLUMN IF NOT EXISTS retry_count    INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS sent_at        TIMESTAMPTZ;

-- Step 5: Back-fill recipient with a placeholder so we can then add NOT NULL
UPDATE notifications SET recipient = 'unknown@himpact.app' WHERE recipient IS NULL;
ALTER TABLE notifications ALTER COLUMN recipient SET NOT NULL;

-- Step 6: Back-fill content so we can add NOT NULL
UPDATE notifications SET content = '' WHERE content IS NULL;
ALTER TABLE notifications ALTER COLUMN content SET NOT NULL;

-- Step 7: Relax event_id to nullable (entity is @ManyToOne with default optional=true)
ALTER TABLE notifications ALTER COLUMN event_id DROP NOT NULL;

-- Step 8: Widen delivery_channel and status to match entity field lengths
ALTER TABLE notifications
    ALTER COLUMN delivery_channel TYPE VARCHAR(30),
    ALTER COLUMN status           TYPE VARCHAR(30);

-- Step 9: Add missing indexes declared in the entity @Table annotation
CREATE INDEX IF NOT EXISTS idx_notifications_channel    ON notifications (delivery_channel);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications (created_at);
