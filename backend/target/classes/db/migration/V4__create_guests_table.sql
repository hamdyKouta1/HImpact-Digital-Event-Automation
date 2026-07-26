-- V4: Create guests table
-- See: project-index/06_Database_Design.md — guests entity
-- See: project-index/03_Functional_Requirements.md — FR-04 Guest Management

CREATE TABLE guests (
    id                  UUID            NOT NULL DEFAULT gen_random_uuid(),
    event_id            UUID            NOT NULL,
    full_name           VARCHAR(255)    NOT NULL,
    mobile              VARCHAR(20),
    email               VARCHAR(255),
    -- Unique code used in the invitation URL
    invitation_code     VARCHAR(64)     NOT NULL UNIQUE,
    invitation_url      VARCHAR(500),
    upload_limit        INT             NOT NULL DEFAULT 30,
    uploaded_count      INT             NOT NULL DEFAULT 0,
    storage_used_mb     DECIMAL(10, 2)  NOT NULL DEFAULT 0.00,
    -- Guest status in the event
    status              VARCHAR(30)     NOT NULL DEFAULT 'INVITED',

    -- Audit columns
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          UUID,
    updated_by          UUID,
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    version             BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_guests PRIMARY KEY (id),
    CONSTRAINT fk_guests_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT ck_guests_status CHECK (status IN ('INVITED', 'REGISTERED', 'ATTENDED', 'DECLINED', 'BLOCKED'))
);

CREATE INDEX idx_guests_event_id         ON guests (event_id);
CREATE INDEX idx_guests_invitation_code  ON guests (invitation_code);
CREATE INDEX idx_guests_mobile           ON guests (mobile);
CREATE INDEX idx_guests_event_status     ON guests (event_id, status);
