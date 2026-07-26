-- V8: Create media_files table
-- See: project-index/06_Database_Design.md — media_files entity
-- See: project-index/03_Functional_Requirements.md — FR-08 Media Upload, FR-09 Storage

CREATE TABLE media_files (
    id                  UUID            NOT NULL DEFAULT gen_random_uuid(),
    event_id            UUID            NOT NULL,
    guest_id            UUID            NOT NULL,
    original_filename   VARCHAR(255)    NOT NULL,
    storage_filename    VARCHAR(255)    NOT NULL,
    mime_type           VARCHAR(100)    NOT NULL,
    file_size           BIGINT          NOT NULL,
    image_width         INT,
    image_height        INT,
    -- Provider abstraction — supports future storage providers (DEC-007, StorageProvider interface)
    storage_provider    VARCHAR(50)     NOT NULL DEFAULT 'GOOGLE_DRIVE',
    -- Full path / file ID in the storage provider
    storage_path        VARCHAR(500)    NOT NULL,
    upload_status       VARCHAR(30)     NOT NULL DEFAULT 'PENDING',
    uploaded_at         TIMESTAMPTZ,

    -- Audit columns
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          UUID,
    updated_by          UUID,
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    version             BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_media_files PRIMARY KEY (id),
    CONSTRAINT fk_media_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_media_guest FOREIGN KEY (guest_id) REFERENCES guests (id),
    CONSTRAINT ck_media_upload_status CHECK (upload_status IN ('PENDING', 'UPLOADING', 'COMPLETED', 'FAILED', 'DELETED')),
    CONSTRAINT ck_media_storage_provider CHECK (storage_provider IN ('GOOGLE_DRIVE', 'LOCAL', 'S3', 'ONEDRIVE'))
);

CREATE INDEX idx_media_event_id         ON media_files (event_id);
CREATE INDEX idx_media_guest_id         ON media_files (guest_id);
CREATE INDEX idx_media_upload_status    ON media_files (upload_status);
CREATE INDEX idx_media_event_guest      ON media_files (event_id, guest_id);
