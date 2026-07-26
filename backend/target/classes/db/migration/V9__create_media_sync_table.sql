-- V9: Create media_sync table (Renamed from upload_queue per PO Decision 8)
-- Tracks the server-side processing, retry history, and synchronization status of media uploads.
-- The client-side offline queue lives in the browser (IndexedDB / Service Worker).
-- The server manages synchronization state and processing audit trail after requests reach the backend.
-- See: project-index/06_Database_Design.md — media_sync entity
-- See: project-index/02_Decision_Log.md — DEC-010 Offline Upload
-- See: project-index/04_Non_Functional_Requirements.md — NFR-04 Reliability

CREATE TABLE media_sync (
    id                  UUID            NOT NULL DEFAULT gen_random_uuid(),
    guest_id            UUID            NOT NULL,
    event_id            UUID            NOT NULL,
    media_file_id       UUID,
    -- Client-generated identifier to prevent duplicate uploads
    local_identifier    VARCHAR(255)    NOT NULL,
    retry_count         INT             NOT NULL DEFAULT 0,
    sync_status         VARCHAR(30)     NOT NULL DEFAULT 'PENDING',
    error_message       TEXT,
    synchronized_at     TIMESTAMPTZ,

    -- Audit columns
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          UUID,
    updated_by          UUID,
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    version             BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_media_sync PRIMARY KEY (id),
    CONSTRAINT fk_media_sync_guest FOREIGN KEY (guest_id) REFERENCES guests (id),
    CONSTRAINT fk_media_sync_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_media_sync_file FOREIGN KEY (media_file_id) REFERENCES media_files (id),
    CONSTRAINT uq_media_sync_local_id UNIQUE (guest_id, local_identifier),
    CONSTRAINT ck_media_sync_status CHECK (sync_status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED'))
);

CREATE INDEX idx_media_sync_guest_status ON media_sync (guest_id, sync_status);
CREATE INDEX idx_media_sync_event_status ON media_sync (event_id, sync_status);
