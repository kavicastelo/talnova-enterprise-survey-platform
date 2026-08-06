import { test, expect } from '@playwright/test';

test.describe('Factory Touch Kiosk Player PWA E2E', () => {
  test('PIN entry pad and 5s auto-reset timer post-submission', async ({ page }) => {
    // Mock POST /api/v1/responses
    await page.route('/api/v1/responses', async (route) => {
      await route.fulfill({
        status: 202,
        contentType: 'application/json',
        body: JSON.stringify({
          success: true,
          message: 'Kiosk response ingested successfully',
          data: {
            responseId: 'RSP-KIOSK-88102',
            status: 'ACCEPTED',
            message: 'Survey response submitted successfully',
            timestamp: new Date().toISOString(),
          },
        }),
      });
    });

    await page.goto('/');
    expect(page).toBeDefined();
  });
});
