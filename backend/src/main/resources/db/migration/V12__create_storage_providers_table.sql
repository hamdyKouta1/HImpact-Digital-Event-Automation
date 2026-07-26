-- V12: Create storage_providers table
-- Abstraction layer for storage providers (Google Drive, S3, OneDrive, etc.)
-- All providers implement the StorageProvider interface — business logic is provider-agnostic.
-- See: project-index/06_Database_Design.md — storage_providers entity
-- See: project-index/02_Decision_Log.md — DEC-007 Storage Strategy

CREATE TABLE storage_providers (
    id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    event_id        UUID            NOT NULL UNIQUE,
    provider_name   VARCHAR(50)     NOT NULL DEFAULT 'GOOGLE_DRIVE',
    provider_type   VARCHAR(50)     NOT NULL DEFAULT 'GOOGLE_DRIVE',
    root_folder_id  VARCHAR(255),
    access_token    TEXT,
    refresh_token   TEXT,
    quota_gb        DECIMAL(10, 2),
    used_gb         DECIMAL(10, 2)  NOT NULL DEFAULT 0.00,
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',

    -- Audit columns
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      UUID,
    updated_by      UUID,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    version         BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_storage_providers PRIMARY KEY (id),
    CONSTRAINT fk_storage_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT ck_storage_provider_type CHECK (provider_type IN ('GOOGLE_DRIVE', 'S3', 'ONEDRIVE', 'LOCAL')),
    CONSTRAINT ck_storage_status CHECK (status IN ('ACTIVE', 'DISCONNECTED', 'QUOTA_EXCEEDED'))
);

CREATE INDEX idx_storage_event_id ON storage_providers (event_id);
