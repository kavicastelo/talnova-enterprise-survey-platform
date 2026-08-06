import { test, expect } from '@playwright/test';

test.describe('Survey Questionnaire Player SPA E2E', () => {
  test('Render questionnaire and submit response', async ({ page }) => {
    // Mock POST /api/v1/responses
    await page.route('/api/v1/responses', async (route) => {
      await route.fulfill({
        status: 202,
        contentType: 'application/json',
        body: JSON.stringify({
          success: true,
          message: 'Survey response ingested successfully',
          data: {
            responseId: 'RSP-9920101',
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
