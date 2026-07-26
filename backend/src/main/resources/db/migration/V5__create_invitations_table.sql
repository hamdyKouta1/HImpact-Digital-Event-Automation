-- V5: Create invitations table
-- See: project-index/06_Database_Design.md — invitations entity
-- See: project-index/03_Functional_Requirements.md — FR-03 Invitation Management

CREATE TABLE invitations (
    id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    event_id        UUID            NOT NULL,
    guest_id        UUID            NOT NULL UNIQUE,
    short_url       VARCHAR(255),
    qr_code         TEXT,
    sent_at         TIMESTAMPTZ,
    opened_at       TIMESTAMPTZ,
    viewed_count    INT             NOT NULL DEFAULT 0,

    -- Audit columns
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      UUID,
    updated_by      UUID,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    version         BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_invitations PRIMARY KEY (id),
    CONSTRAINT fk_invitations_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_invitations_guest FOREIGN KEY (guest_id) REFERENCES guests (id)
);

CREATE INDEX idx_invitations_event_id ON invitations (event_id);
CREATE INDEX idx_invitations_guest_id ON invitations (guest_id);
