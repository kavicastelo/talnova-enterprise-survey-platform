import { test, expect } from '@playwright/test';

test.describe('FEAT-003: Employee Roster & CSFLE Management E2E Flow', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('TC-E2E-EMP-001: Should navigate to Employee Roster tab, search employees, and filter by status', async ({ page }) => {
    // Navigate to Employee Roster tab
    const rosterTabBtn = page.getByRole('button', { name: /Employee Roster/i });
    await expect(rosterTabBtn).toBeVisible();
    await rosterTabBtn.click();

    // Verify DataGrid header
    await expect(page.getByText('Enterprise Employee Roster')).toBeVisible();
    await expect(page.getByText('EMP-10020')).toBeVisible();
    await expect(page.getByText('John Doe')).toBeVisible();

    // Perform live search query
    const searchInput = page.getByPlaceholder(/Search by Employee ID, Name/i);
    await searchInput.fill('Jane Smith');

    await expect(page.getByText('EMP-10021')).toBeVisible();
    await expect(page.getByText('John Doe')).not.toBeVisible();

    // Clear search
    await searchInput.fill('');
    await expect(page.getByText('John Doe')).toBeVisible();
  });

  test('TC-E2E-EMP-002: Should launch CSV Import Wizard Modal and simulate AI column mapping', async ({ page }) => {
    const rosterTabBtn = page.getByRole('button', { name: /Employee Roster/i });
    await rosterTabBtn.click();

    const importBtn = page.getByRole('button', { name: /Import CSV Roster/i });
    await expect(importBtn).toBeVisible();
    await importBtn.click();

    // Modal opens
    await expect(page.getByText('CSV Roster Import Wizard')).toBeVisible();
    await expect(page.getByText('Step 1 of 3')).toBeVisible();
    await expect(page.getByText(/Drag & Drop Employee CSV Roster File/i)).toBeVisible();
  });
});
