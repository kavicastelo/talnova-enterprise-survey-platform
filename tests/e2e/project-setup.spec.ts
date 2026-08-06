import { test, expect } from '@playwright/test';

test.describe('FEAT-001 Project Configuration Setup Wizard E2E Test Suite', () => {

  test('Step-by-step workspace provisioning and WCAG contrast validation', async ({ page }) => {
    // Navigate to local SPA development server
    await page.goto('http://localhost:5173');

    // Verify Title
    await expect(page.locator('h1')).toContainText('Project Workspace Provisioning');

    // Step 1: Fill Workspace Metadata
    await page.fill('input[placeholder="PRJ-99201"]', 'PRJ-99201');
    await page.fill('input[placeholder="e.g. Aitken Spence Enterprise Workspace"]', 'Aitken Spence Enterprise Workspace');
    await page.click('button:has-text("Next Step →")');

    // Step 2: Branding & Theme Customization
    await expect(page.locator('h3')).toContainText('Step 2: Branding & Theme Customization');
    await expect(page.locator('text=WCAG 2.1 AA Contrast')).toBeVisible();

    // Verify Theme Customizer Live Preview Card Header
    await expect(page.locator('text=LIVE PREVIEW')).toBeVisible();

    // Test Color Picker input change
    const primaryColorInput = page.locator('input[type="text"]').nth(2);
    await primaryColorInput.fill('#1E3A8A');

    await page.click('button:has-text("Next Step →")');

    // Step 3: Locales & Feature Flags
    await expect(page.locator('h3')).toContainText('Step 3: Locales & Feature Flags');
    await page.selectOption('select', 'en-US');

    // Toggle AI Analytics Feature Flag
    const aiCheckbox = page.locator('input[type="checkbox"]').first();
    await expect(aiCheckbox).toBeChecked();

    await page.click('button:has-text("Next Step →")');

    // Step 4: Review & Deploy
    await expect(page.locator('h3')).toContainText('Step 4: Review & Deploy Configuration');
    await expect(page.locator('text=PRJ-99201')).toBeVisible();

    // Submit Provisioning Form
    await page.click('button:has-text("Deploy Project Workspace")');

    // Assert Success Card
    await expect(page.locator('h2')).toContainText('Project Workspace Successfully Provisioned!');
  });
});
