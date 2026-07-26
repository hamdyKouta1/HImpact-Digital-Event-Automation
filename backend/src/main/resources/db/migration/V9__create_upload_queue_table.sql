-- V9: Create upload_queue table
-- Tracks the server-side synchronization state of offline uploads.
-- The client-side offline queue lives in the browser (IndexedDB / Service Worker).
-- This table records what has been synced and allows resume after interruption.
-- See: project-index/06_Database_Design.md — upload_queue entity
-- See: project-index/02_Decision_Log.md — DEC-010 Offline Upload
-- See: project-index/04_Non_Functional_Requirements.md — NFR-04 Reliability

CREATE TABLE upload_queue (
    id                  UUID            NOT NULL DEFAULT gen_random_uuid(),
    guest_id            UUID            NOT NULL,
    event_id            UUID            NOT NULL,
    -- Client-generated identifier to prevent duplicate uploads
    local_identifier    VARCHAR(255)    NOT NULL,
    retry_count         INT             NOT NULL DEFAULT 0,
    status              VARCHAR(30)     NOT NULL DEFAULT 'QUEUED',
    error_message       TEXT,
    synchronized_at     TIMESTAMPTZ,

    -- Audit columns
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          UUID,
    updated_by          UUID,
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    version             BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_upload_queue PRIMARY KEY (id),
    CONSTRAINT fk_upload_queue_guest FOREIGN KEY (guest_id) REFERENCES guests (id),
    CONSTRAINT fk_upload_queue_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT uq_upload_queue_local_id UNIQUE (guest_id, local_identifier),
    CONSTRAINT ck_upload_queue_status CHECK (status IN ('QUEUED', 'UPLOADING', 'COMPLETED', 'FAILED'))
);

CREATE INDEX idx_upload_queue_guest_status ON upload_queue (guest_id, status);
