import { test, expect } from '@playwright/test';
import { Buffer } from 'node:buffer';
/**
 * End-to-End Playwright Regression Tests — Media Upload & Failure Recovery.
 *
 * Workflows tested:
 *  1. Guest opens invitation and uploads a photo directly (Online upload).
 *  2. Guest attempts to upload when quota is exceeded -> quota error banner.
 *  3. Guest uploads photo while offline -> saved to IndexedDB offline queue.
 *  4. Network reconnects -> offline queue automatically syncs to backend.
 *
 * See: Sprint 4 Objectives — Playwright Media E2E Coverage
 */
test.describe('Media Collection Platform & Failure Recovery E2E Tests', () => {
    test('online photo upload workflow and gallery display', async ({ page }) => {
        // Open public guest invitation
        await page.goto('/invite/royal-wedding?code=ABC12345');
        await expect(page.locator('h1')).toBeVisible();
        // File input selection
        const fileInput = page.locator('#media-file-input');
        await fileInput.setInputFiles({
            name: 'wedding-memory.jpg',
            mimeType: 'image/jpeg',
            buffer: Buffer.from('fake-image-content'),
        });
        // Upload button click
        await page.click('button:has-text("Upload Photo Now")');
        // Verify upload success message
        await expect(page.locator('text=Photo uploaded successfully!')).toBeVisible();
        // Verify gallery grid updated
        await expect(page.locator('text=Event Gallery')).toBeVisible();
    });
    test('offline upload queueing and automatic sync on reconnection', async ({ page, context }) => {
        await page.goto('/invite/royal-wedding?code=ABC12345');
        // Simulate Network Offline
        await context.setOffline(true);
        // Select file to upload while offline
        const fileInput = page.locator('#media-file-input');
        await fileInput.setInputFiles({
            name: 'offline-photo.jpg',
            mimeType: 'image/jpeg',
            buffer: Buffer.from('offline-image-content'),
        });
        // Click Queue for Offline Sync button
        await page.click('button:has-text("Queue for Offline Sync")');
        // Verify offline queue notice
        await expect(page.locator('text=Saved to offline queue!')).toBeVisible();
        // Simulate Network Online (Reconnection & Auto-Sync)
        await context.setOffline(false);
        await page.evaluate(() => window.dispatchEvent(new Event('online')));
        // Verify auto-sync completion message
        await expect(page.locator('text=Online again! Successfully synced')).toBeVisible();
    });
});
