-- V11: Create payments table
-- Manual payment workflow (InstaPay, Vodafone Cash).
-- Payment providers are abstracted for future gateway integration.
-- See: project-index/06_Database_Design.md — payments entity
-- See: project-index/03_Functional_Requirements.md — FR-10 Payments
-- See: project-index/02_Decision_Log.md — DEC-005 Payment Strategy

CREATE TABLE payments (
    id                  UUID            NOT NULL DEFAULT gen_random_uuid(),
    event_id            UUID            NOT NULL,
    package_id          UUID,
    payment_method      VARCHAR(50)     NOT NULL,
    amount              DECIMAL(10, 2)  NOT NULL,
    currency            VARCHAR(10)     NOT NULL DEFAULT 'EGP',
    -- Reference number provided by customer (receipt number, transaction ID, etc.)
    payment_reference   VARCHAR(255),
    payment_status      VARCHAR(30)     NOT NULL DEFAULT 'AWAITING_PAYMENT',
    approved_by         UUID,
    approved_at         TIMESTAMPTZ,
    notes               TEXT,

    -- Audit columns
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          UUID,
    updated_by          UUID,
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    version             BIGINT          NOT NULL DEFAULT 0,

    CONSTRAINT pk_payments PRIMARY KEY (id),
    CONSTRAINT fk_payments_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_payments_package FOREIGN KEY (package_id) REFERENCES packages (id),
    CONSTRAINT ck_payments_method CHECK (payment_method IN ('INSTAPAY', 'VODAFONE_CASH', 'BANK_TRANSFER', 'OTHER')),
    CONSTRAINT ck_payments_status CHECK (payment_status IN ('AWAITING_PAYMENT', 'PAYMENT_SUBMITTED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED'))
);

CREATE INDEX idx_payments_event_id   ON payments (event_id);
CREATE INDEX idx_payments_status     ON payments (payment_status);
