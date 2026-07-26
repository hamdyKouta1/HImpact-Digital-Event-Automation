-- V3: Create events table
-- The event is the core business entity of the platform.
-- Architecture is event-agnostic to support future event types (DEC-003).
-- See: project-index/06_Database_Design.md — events entity
-- See: project-index/03_Functional_Requirements.md — FR-02 Event Management

CREATE TABLE events (
    id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    owner_id        UUID            NOT NULL,
    title           VARCHAR(255)    NOT NULL,
    -- Generic event type — supports Wedding, Birthday, Corporate etc. (PI-00 Product Definition)
    event_type      VARCHAR(50)     NOT NULL DEFAULT 'WEDDING',
    -- Wedding-specific display fields (optional for other event types)
    bride_name      VARCHAR(100),
    groom_name      VARCHAR(100),
    description     TEXT,
    venue_name      VARCHAR(255),
    venue_address   TEXT,
    google_maps_url VARCHAR(500),
    event_date      DATE            NOT NULL,
    start_time      TIME,
    end_time        TIME,
    cover_image     VARCHAR(500),
    package_id      UUID,
    theme_id        UUID,
    -- Event lifecycle status
    status          VARCHAR(30)     NOT NULL DEFAULT 'DRAFT',
    -- Unique slug for invitation URLs
    slug            VARCHAR(100)    UNIQUE,

    -- Audit columns
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      UUID,
    updated_by      UUID,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    version         BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_events PRIMARY KEY (id),
    CONSTRAINT fk_events_owner FOREIGN KEY (owner_id) REFERENCES users (id),
    CONSTRAINT fk_events_package FOREIGN KEY (package_id) REFERENCES packages (id),
    CONSTRAINT fk_events_theme FOREIGN KEY (theme_id) REFERENCES themes (id),
    CONSTRAINT ck_events_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED', 'SUSPENDED')),
    CONSTRAINT ck_events_type CHECK (event_type IN ('WEDDING', 'BIRTHDAY', 'GRADUATION', 'CORPORATE', 'CONFERENCE', 'OTHER'))
);

CREATE INDEX idx_events_owner_id     ON events (owner_id);
CREATE INDEX idx_events_status       ON events (status) WHERE is_deleted = FALSE;
CREATE INDEX idx_events_event_date   ON events (event_date);
CREATE INDEX idx_events_slug         ON events (slug);
