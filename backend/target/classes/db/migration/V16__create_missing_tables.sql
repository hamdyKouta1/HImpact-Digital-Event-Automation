-- V16: Create missing tables for audit_logs, dashboard_statistics, feature_flags, refresh_tokens
-- These entities existed in Java code but lacked corresponding Flyway migrations.
-- See: AuditLog.java, DashboardStatistics.java, FeatureFlag.java, RefreshToken.java

-- ── audit_logs ────────────────────────────────────────────────────────────────
-- Immutable event log for every administrative action (Sprint 5 Audit System).
CREATE TABLE audit_logs (
    id          UUID        NOT NULL DEFAULT gen_random_uuid(),
    user_id     UUID,
    ip_address  VARCHAR(50),
    action      VARCHAR(100) NOT NULL,
    entity_name VARCHAR(100) NOT NULL,
    entity_id   VARCHAR(100),
    old_value   TEXT,
    new_value   TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_audit_logs PRIMARY KEY (id)
);

CREATE INDEX idx_audit_user_id  ON audit_logs (user_id);
CREATE INDEX idx_audit_action   ON audit_logs (action);
CREATE INDEX idx_audit_entity   ON audit_logs (entity_name, entity_id);

-- ── dashboard_statistics ──────────────────────────────────────────────────────
-- Pre-aggregated platform statistics for instant dashboard loads (Workstream D).
-- Uses a string primary key to support both SINGLETON_GLOBAL and EVENT_{id} rows.
CREATE TABLE dashboard_statistics (
    id                       VARCHAR(50)     NOT NULL,
    total_users              BIGINT          NOT NULL DEFAULT 0,
    total_events             BIGINT          NOT NULL DEFAULT 0,
    published_events         BIGINT          NOT NULL DEFAULT 0,
    total_guests             BIGINT          NOT NULL DEFAULT 0,
    total_invitation_views   BIGINT          NOT NULL DEFAULT 0,
    total_rsvps              BIGINT          NOT NULL DEFAULT 0,
    total_uploads            BIGINT          NOT NULL DEFAULT 0,
    total_storage_bytes      BIGINT          NOT NULL DEFAULT 0,
    total_revenue            NUMERIC(12, 2)  NOT NULL DEFAULT 0.00,
    updated_at               TIMESTAMPTZ     NOT NULL DEFAULT now(),

    CONSTRAINT pk_dashboard_statistics PRIMARY KEY (id)
);

-- Seed the global singleton row so AnalyticsService never returns empty.
INSERT INTO dashboard_statistics (id, updated_at)
VALUES ('SINGLETON_GLOBAL', now())
ON CONFLICT (id) DO NOTHING;

-- ── feature_flags ─────────────────────────────────────────────────────────────
-- Database-configurable feature toggles (Sprint 5 Feature Flags Requirement).
CREATE TABLE feature_flags (
    id          UUID        NOT NULL DEFAULT gen_random_uuid(),
    flag_name   VARCHAR(100) NOT NULL,
    enabled     BOOLEAN     NOT NULL DEFAULT TRUE,
    description VARCHAR(255),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_feature_flags     PRIMARY KEY (id),
    CONSTRAINT uq_feature_flag_name UNIQUE (flag_name)
);

-- Seed default feature flags matching documented feature set.
INSERT INTO feature_flags (flag_name, enabled, description) VALUES
    ('GOOGLE_DRIVE',   TRUE,  'Enable Google Drive cloud storage integration'),
    ('PAYMENTS',       FALSE, 'Enable payment processing (Stripe/Paddle) — disabled until Sprint 7'),
    ('MEDIA',          TRUE,  'Enable guest media upload and sync features'),
    ('COMMENTS',       TRUE,  'Enable comment thread on media items'),
    ('RSVP',           TRUE,  'Enable RSVP submission and tracking'),
    ('NOTIFICATIONS',  TRUE,  'Enable email and push notification dispatch')
ON CONFLICT (flag_name) DO NOTHING;

-- ── refresh_tokens ────────────────────────────────────────────────────────────
-- JWT session renewal tokens (Sprint 6 Workstream B).
-- FK to users table (created in V1).
CREATE TABLE refresh_tokens (
    id          UUID        NOT NULL DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL,
    token       VARCHAR(255) NOT NULL,
    expiry_date TIMESTAMPTZ NOT NULL,
    revoked     BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_refresh_tokens        PRIMARY KEY (id),
    CONSTRAINT uq_refresh_token         UNIQUE (token),
    CONSTRAINT fk_refresh_tokens_user   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_token_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_token_token   ON refresh_tokens (token);
