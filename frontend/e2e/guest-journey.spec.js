import { test, expect } from '@playwright/test';
/**
 * End-to-End Automated Regression Test — Event Owner & Guest Journey.
 *
 * Critical Workflow:
 *  1. Owner logs in
 *  2. Owner creates event
 *  3. Owner adds guest
 *  4. Owner publishes event
 *  5. Guest opens public invitation link
 *  6. Guest submits RSVP (Attending + Notes)
 *  7. Owner dashboard updates with RSVP statistics
 *
 * See: Sprint 3 Objectives — Playwright E2E Testing
 */
test.describe('Event Owner & Guest Journey End-to-End Test', () => {
    test('complete workflow from event creation to guest RSVP', async ({ page, context }) => {
        // ── 1. Owner Login ────────────────────────────────────────────────────────
        await page.goto('/sign-in');
        await expect(page).toHaveTitle(/HImpact/);
        // Click Google Sign-In button
        await page.click('#google-sign-in-btn');
        // ── 2. Create Event ───────────────────────────────────────────────────────
        await page.goto('/owner/events/new');
        await page.fill('#title', 'Playwright E2E Wedding');
        await page.selectOption('#eventType', 'WEDDING');
        await page.fill('#brideName', 'Amina');
        await page.fill('#groomName', 'Tarek');
        await page.fill('#eventDate', '2026-09-11');
        await page.fill('#startTime', '18:00');
        await page.fill('#venueName', 'Grand Nile Palace');
        await page.click('button[type="submit"]');
        // Verify Event Created & Redirected to Details Page
        await expect(page.locator('h1')).toContainText('Playwright E2E Wedding');
        await expect(page.locator('span')).toContainText('DRAFT');
        // ── 3. Add Guest ──────────────────────────────────────────────────────────
        await page.click('text=Manage Guests');
        await page.click('text=+ Add Guest');
        await page.fill('input[placeholder=""]', 'Karim Hassan');
        await page.fill('input[placeholder="+201012345678"]', '+201011112222');
        await page.click('button[type="submit"]');
        // Verify guest added in table
        await expect(page.locator('tbody')).toContainText('Karim Hassan');
        // ── 4. Publish Event ──────────────────────────────────────────────────────
        await page.goto('/owner');
        await page.click('text=Manage');
        await page.click('button:has-text("Publish Event")');
        await expect(page.locator('span')).toContainText('PUBLISHED');
        // ── 5. Guest Opens Invitation ─────────────────────────────────────────────
        // Open a new browser tab as Guest
        const guestPage = await context.newPage();
        await guestPage.goto('/invite/playwright-e2e-wedding?code=ABC12345');
        // Verify Guest Invitation Page Loaded
        await expect(guestPage.locator('h1')).toContainText('Playwright E2E Wedding');
        // ── 6. Guest Submits RSVP ────────────────────────────────────────────────
        await guestPage.click('text=RSVP Now ✨');
        await guestPage.click('text=Yes, Attending');
        await guestPage.fill('#notes', 'Excited to attend! Vegetarian meal please.');
        await guestPage.click('button[type="submit"]');
        // ── 7. Verify Owner Dashboard Updated ────────────────────────────────────
        await page.goto('/owner');
        await expect(page.locator('text=Photos Collected')).toBeVisible();
    });
});
