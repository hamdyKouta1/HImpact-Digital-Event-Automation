-- V18: Reconcile payments table schema with Payment.java entity
--
-- The Payment entity has evolved significantly since V11:
-- 1. Column rename: payment_status → payment_state
-- 2. New columns: receipt_image_url, rejection_reason
-- 3. payment_reference changed from nullable to NOT NULL
-- 4. payment_state values (PaymentState enum): SUBMITTED, UNDER_REVIEW, APPROVED, ACTIVATED, REJECTED
--    (replaces old AWAITING_PAYMENT / PAYMENT_SUBMITTED values)
-- 5. Index rename: idx_payments_status → idx_payments_state

-- Step 1: Drop the old CHECK constraints that reference stale column / value names
ALTER TABLE payments
    DROP CONSTRAINT IF EXISTS ck_payments_status,
    DROP CONSTRAINT IF EXISTS ck_payments_method;

-- Step 2: Drop the old index on the stale column name
DROP INDEX IF EXISTS idx_payments_status;

-- Step 3: Rename the status column to match the entity field name
ALTER TABLE payments RENAME COLUMN payment_status TO payment_state;

-- Step 4: Back-fill payment_state values to match the new PaymentState enum.
-- Old value → New value mapping:
--   AWAITING_PAYMENT  → SUBMITTED
--   PAYMENT_SUBMITTED → SUBMITTED
--   UNDER_REVIEW      → UNDER_REVIEW (unchanged)
--   APPROVED          → APPROVED     (unchanged)
--   REJECTED          → REJECTED     (unchanged)
UPDATE payments SET payment_state = 'SUBMITTED'    WHERE payment_state IN ('AWAITING_PAYMENT', 'PAYMENT_SUBMITTED');
UPDATE payments SET payment_state = 'UNDER_REVIEW' WHERE payment_state = 'UNDER_REVIEW';

-- Step 5: Add missing columns required by the entity
ALTER TABLE payments
    ADD COLUMN IF NOT EXISTS receipt_image_url  VARCHAR(500),
    ADD COLUMN IF NOT EXISTS rejection_reason   TEXT;

-- Step 6: Enforce NOT NULL on payment_reference (entity sets nullable = false).
--         Back-fill any nulls with a placeholder before constraining.
UPDATE payments SET payment_reference = 'MIGRATED-NO-REF' WHERE payment_reference IS NULL;
ALTER TABLE payments ALTER COLUMN payment_reference SET NOT NULL;

-- Step 7: Re-create the index under the new name
CREATE INDEX IF NOT EXISTS idx_payments_state ON payments (payment_state);

-- Step 8: Restore CHECK constraints aligned with current entity / enum values
ALTER TABLE payments
    ADD CONSTRAINT ck_payments_method CHECK (payment_method IN ('INSTAPAY', 'VODAFONE_CASH', 'BANK_TRANSFER', 'OTHER')),
    ADD CONSTRAINT ck_payments_state  CHECK (payment_state  IN ('SUBMITTED', 'UNDER_REVIEW', 'APPROVED', 'ACTIVATED', 'REJECTED'));
