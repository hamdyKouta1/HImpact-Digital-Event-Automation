-- V2: Create packages and themes tables
-- These are referenced by events, so they must be created first.
-- See: project-index/06_Database_Design.md — packages, themes entities

-- ── Packages ──────────────────────────────────────────────────────────────────
-- Configurable commercial plans (pricing is never hardcoded — PI-01, DEC-006)
CREATE TABLE packages (
    id                  UUID            NOT NULL DEFAULT gen_random_uuid(),
    package_name        VARCHAR(100)    NOT NULL,
    max_guests          INT             NOT NULL,
    max_uploads_per_guest INT           NOT NULL,
    storage_limit_gb    DECIMAL(10, 2)  NOT NULL,
    price               DECIMAL(10, 2)  NOT NULL,
    currency            VARCHAR(10)     NOT NULL DEFAULT 'EGP',
    active              BOOLEAN         NOT NULL DEFAULT TRUE,
    display_order       INT             NOT NULL DEFAULT 0,

    -- Audit columns
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          UUID,
    updated_by          UUID,
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    version             BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_packages PRIMARY KEY (id)
);

-- ── Themes ────────────────────────────────────────────────────────────────────
-- Visual themes for events. Admin-managed.
-- See: project-index/03_Functional_Requirements.md — FR-11 Theme Management
CREATE TABLE themes (
    id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    theme_name      VARCHAR(100)    NOT NULL,
    primary_color   VARCHAR(7)      NOT NULL DEFAULT '#3B82F6',
    secondary_color VARCHAR(7)      NOT NULL DEFAULT '#8B5CF6',
    preview_image   VARCHAR(500),
    premium         BOOLEAN         NOT NULL DEFAULT FALSE,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,

    -- Audit columns
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      UUID,
    updated_by      UUID,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    version         BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_themes PRIMARY KEY (id)
);
