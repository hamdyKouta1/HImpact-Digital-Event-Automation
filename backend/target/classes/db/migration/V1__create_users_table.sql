-- V1: Create users table
-- See: project-index/06_Database_Design.md — users entity
-- See: project-index/03_Functional_Requirements.md — FR-01 Authentication

CREATE TABLE users (
    id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    google_id       VARCHAR(128)    NOT NULL,
    full_name       VARCHAR(255)    NOT NULL,
    email           VARCHAR(255)    NOT NULL,
    mobile_number   VARCHAR(20),
    profile_picture VARCHAR(500),
    role            VARCHAR(20)     NOT NULL DEFAULT 'GUEST',
    status          VARCHAR(30)     NOT NULL DEFAULT 'PENDING_VERIFICATION',
    mobile_verified BOOLEAN         NOT NULL DEFAULT FALSE,
    last_login      TIMESTAMPTZ,

    -- Audit columns (required on all business entities per PI-06)
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      UUID,
    updated_by      UUID,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    version         BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_google_id UNIQUE (google_id),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT ck_users_role CHECK (role IN ('ADMIN', 'OWNER', 'GUEST')),
    CONSTRAINT ck_users_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'PENDING_VERIFICATION'))
);

-- Indexes (see: project-index/06_Database_Design.md — Index Strategy)
CREATE INDEX idx_users_google_id     ON users (google_id);
CREATE INDEX idx_users_email         ON users (email);
CREATE INDEX idx_users_status        ON users (status) WHERE is_deleted = FALSE;
