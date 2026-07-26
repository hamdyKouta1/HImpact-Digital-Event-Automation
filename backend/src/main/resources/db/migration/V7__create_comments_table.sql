-- V7: Create comments table (Digital Congratulations)
-- See: project-index/06_Database_Design.md — comments entity
-- See: project-index/03_Functional_Requirements.md — FR-07 Gallery (comments)

CREATE TABLE comments (
    id          UUID        NOT NULL DEFAULT gen_random_uuid(),
    guest_id    UUID        NOT NULL,
    event_id    UUID        NOT NULL,
    message     TEXT        NOT NULL,

    -- Audit columns
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by  UUID,
    updated_by  UUID,
    is_deleted  BOOLEAN     NOT NULL DEFAULT FALSE,
    version     BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comments_guest FOREIGN KEY (guest_id) REFERENCES guests (id),
    CONSTRAINT fk_comments_event FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE INDEX idx_comments_event_id ON comments (event_id);
CREATE INDEX idx_comments_guest_id ON comments (guest_id);
