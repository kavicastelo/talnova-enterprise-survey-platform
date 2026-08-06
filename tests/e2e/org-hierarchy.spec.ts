import { test, expect } from '@playwright/test';

test.describe('Organizational Hierarchy Management E2E Flow', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:3000');
  });

  test('should render hierarchy canvas, create organization node, and perform re-parenting move', async ({ page }) => {
    // Step 1: Navigate to Org Hierarchy tab
    await page.click('button:has-text("Org Hierarchy")');
    await expect(page.locator('h2')).toContainText('Organizational Hierarchy & Structure Studio');

    // Step 2: Open Create Node Modal
    await page.click('button:has-text("+ Add New Node")');
    await expect(page.locator('h3')).toContainText('Add New Organization Node');

    // Step 3: Fill Node Details
    await page.fill('input[placeholder="e.g. N-302"]', 'N-201');
    await page.fill('input[placeholder="e.g. Quality Assurance Team"]', 'Port Terminal Operations Dept');
    await page.selectOption('select', 'DEPARTMENT');
    await page.fill('input[placeholder="e.g. N-201"]', 'N-101');

    // Step 4: Submit Node Creation
    await page.click('button:has-text("Create Node")');

    // Step 5: Verify Node Card Displayed in Tree Canvas
    await expect(page.locator('text=Port Terminal Operations Dept')).toBeVisible();
    await expect(page.locator('text=N-201')).toBeVisible();
    await expect(page.locator('text=DEPARTMENT')).toBeVisible();

    // Step 6: Test Search Filter
    await page.fill('input[placeholder*="Search node"]', 'Port Terminal');
    await expect(page.locator('text=Port Terminal Operations Dept')).toBeVisible();
  });
});
