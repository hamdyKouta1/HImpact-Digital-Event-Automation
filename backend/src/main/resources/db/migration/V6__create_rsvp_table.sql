-- V6: Create RSVP table
-- See: project-index/06_Database_Design.md — rsvp entity
-- See: project-index/03_Functional_Requirements.md — FR-05 RSVP

CREATE TABLE rsvp (
    id                  UUID            NOT NULL DEFAULT gen_random_uuid(),
    guest_id            UUID            NOT NULL UNIQUE,
    event_id            UUID            NOT NULL,
    attendance_status   VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    attendee_count      INT             NOT NULL DEFAULT 1,
    response_time       TIMESTAMPTZ,
    notes               TEXT,

    -- Audit columns
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          UUID,
    updated_by          UUID,
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    version             BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_rsvp PRIMARY KEY (id),
    CONSTRAINT fk_rsvp_guest FOREIGN KEY (guest_id) REFERENCES guests (id),
    CONSTRAINT fk_rsvp_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT ck_rsvp_status CHECK (attendance_status IN ('PENDING', 'ACCEPTED', 'DECLINED', 'MAYBE'))
);

CREATE INDEX idx_rsvp_event_id   ON rsvp (event_id);
CREATE INDEX idx_rsvp_guest_id   ON rsvp (guest_id);
CREATE INDEX idx_rsvp_status     ON rsvp (event_id, attendance_status);
