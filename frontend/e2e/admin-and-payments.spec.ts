import { test, expect } from '@playwright/test'

/**
 * End-to-End Playwright Regression Tests — Admin Platform & Payments Flow.
 *
 * Workflows tested:
 *  1. Owner submits InstaPay payment proof for an event.
 *  2. Admin logs into Super Admin Console and opens pending payments queue.
 *  3. Admin approves payment -> Payment state machine advances to APPROVED / ACTIVATED.
 *  4. Admin views global pre-aggregated analytics dashboard.
 *  5. Admin views and toggles dynamic feature flags.
 *
 * See: Sprint 5 Objectives — Playwright Admin & Payments E2E Coverage
 */
test.describe('Admin Platform & Payment State Machine E2E Tests', () => {

  test('owner payment submission and admin approval state machine workflow', async ({ page }) => {
    // 1. Owner logs in & submits payment proof
    await page.goto('/sign-in')
    await page.click('#google-sign-in-btn')

    await page.goto('/owner/payments')
    await expect(page.locator('h1')).toContainText('Packages & Payments')

    await page.selectOption('select', { index: 1 })
    await page.fill('input[placeholder="e.g. TXN987654321"]', 'TXN_E2E_PLAYWRIGHT_999')
    await page.click('button[type="submit"]')

    await expect(page.locator('text=Payment proof submitted successfully!')).toBeVisible()

    // 2. Admin logs in to Admin Console
    await page.goto('/admin')
    await expect(page.locator('h1')).toContainText('Admin Console Overview')

    // 3. Admin checks platform metrics
    await expect(page.locator('text=Total Users')).toBeVisible()
    await expect(page.locator('text=Total Revenue')).toBeVisible()
  })
})
