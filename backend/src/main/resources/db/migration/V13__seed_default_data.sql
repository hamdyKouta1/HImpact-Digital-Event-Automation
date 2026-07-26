-- V13: Seed default data
-- Provides initial packages and themes so the application is immediately usable.
-- Pricing is configurable — these are starting values only, not hardcoded limits.
-- See: project-index/01_Product_Strategy.md — Pricing Strategy
-- See: project-index/02_Decision_Log.md — DEC-006 Pricing Model

-- ── Default Packages ─────────────────────────────────────────────────────────
INSERT INTO packages (id, package_name, max_guests, max_uploads_per_guest, storage_limit_gb, price, currency, active, display_order)
VALUES
    (gen_random_uuid(), 'Starter',  100, 30, 3.00,  200.00, 'EGP', TRUE, 1),
    (gen_random_uuid(), 'Standard', 200, 30, 6.00,  300.00, 'EGP', TRUE, 2),
    (gen_random_uuid(), 'Premium',  500, 50, 15.00, 500.00, 'EGP', TRUE, 3);

-- ── Default Themes ────────────────────────────────────────────────────────────
INSERT INTO themes (id, theme_name, primary_color, secondary_color, premium, active)
VALUES
    (gen_random_uuid(), 'Classic White',   '#FFFFFF', '#D4AF37', FALSE, TRUE),
    (gen_random_uuid(), 'Midnight Blue',   '#0F172A', '#3B82F6', FALSE, TRUE),
    (gen_random_uuid(), 'Rose Garden',     '#FDF2F8', '#EC4899', FALSE, TRUE),
    (gen_random_uuid(), 'Golden Elegance', '#1A1A2E', '#FFD700', TRUE,  TRUE);
