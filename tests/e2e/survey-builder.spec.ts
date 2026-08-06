import { test, expect } from '@playwright/test';

test.describe('FEAT-004 Survey Builder Studio E2E Test Suite', () => {

  test('Interactive Survey Page, Section & Real-Time Logic Simulator Flow', async ({ page }) => {
    // Navigate to local SPA development server
    await page.goto('http://localhost:5173');

    // Select Survey Builder Studio Tab
    await page.click('button:has-text("Survey Builder Studio")');

    // Verify Header Banner
    await expect(page.locator('h1')).toContainText('2026 Annual Employee Engagement Survey');
    await expect(page.locator('text=DRAFT v1')).toBeVisible();

    // Add Page to Canvas
    await page.click('button:has-text("+ Add Page")');
    await expect(page.locator('text=Survey Structure (3 Pages)')).toBeVisible();

    // Select Question Card and Verify Inspector Panel
    await page.click('text=My direct manager provides clear direction.');
    await expect(page.locator('h3')).toContainText('Question Properties Inspector');

    // Launch Real-Time Logic Simulator Modal
    await page.click('button:has-text("📱 Launch Logic Simulator")');
    await expect(page.locator('h3')).toContainText('Real-Time Survey Logic & Multi-Device Simulator');

    // Switch Viewport Frame Mode to Mobile Device
    await page.click('button:has-text("📱 Mobile View (375px)")');

    // Select Answer and Advance Next Page
    await page.click('button:has-text("Strongly Disagree")');
    await page.click('button:has-text("Next Page →")');

    // Verify Audit Log Branching Trigger
    await expect(page.locator('text=⚡ Branch Jump')).toBeVisible();

    // Close Simulator
    await page.click('button:has-text("Close")');
  });
});
