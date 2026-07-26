-- V10: Create notifications table
-- Supports multi-channel notifications: Email, WhatsApp, Browser Push.
-- See: project-index/06_Database_Design.md — notifications entity
-- See: project-index/03_Functional_Requirements.md — FR-06 Notification Engine
-- See: project-index/02_Decision_Log.md — DEC-011 Notifications

CREATE TABLE notifications (
    id                  UUID            NOT NULL DEFAULT gen_random_uuid(),
    event_id            UUID            NOT NULL,
    guest_id            UUID,
    notification_type   VARCHAR(50)     NOT NULL,
    delivery_channel    VARCHAR(20)     NOT NULL,
    scheduled_at        TIMESTAMPTZ,
    delivered_at        TIMESTAMPTZ,
    status              VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    error_message       TEXT,

    -- Audit columns
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          UUID,
    updated_by          UUID,
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    version             BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_notifications PRIMARY KEY (id),
    CONSTRAINT fk_notifications_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_notifications_guest FOREIGN KEY (guest_id) REFERENCES guests (id),
    CONSTRAINT ck_notifications_channel CHECK (delivery_channel IN ('EMAIL', 'WHATSAPP', 'PUSH')),
    CONSTRAINT ck_notifications_status CHECK (status IN ('PENDING', 'SENT', 'DELIVERED', 'FAILED', 'CANCELLED'))
);

CREATE INDEX idx_notifications_event_id     ON notifications (event_id);
CREATE INDEX idx_notifications_scheduled    ON notifications (scheduled_at) WHERE status = 'PENDING';
CREATE INDEX idx_notifications_status       ON notifications (status);
